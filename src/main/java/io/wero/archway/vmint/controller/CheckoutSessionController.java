package io.wero.archway.vmint.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import io.wero.archway.vmint.config.AppConfig;
import io.wero.archway.vmint.config.properties.StripeProperties;
import io.wero.archway.vmint.model.CheckoutSessionRequest;
import io.wero.archway.vmint.service.Web3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/api")
public class CheckoutSessionController {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutSessionController.class);
    private final String domain;
    private final StripeProperties stripeProperties;
    private final Web3Service web3Service;

    public CheckoutSessionController(AppConfig appConfig, Web3Service web3Service) {
        this.stripeProperties = appConfig.getStripeProperties();
        this.web3Service = web3Service;
        this.domain = appConfig.getDomain();
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Void> createCheckoutSession(@RequestBody CheckoutSessionRequest request) {
        String priceId = getPriceId(request.collectionId());

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(domain + "/home.html")
                .setCancelUrl(domain + "/home.html")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice(priceId)
                                .build())
                .build();
        try {
            Session session = Session.create(params);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", session.getUrl());
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (StripeException e) {
            throw new IllegalStateException(e);
        }
    }

    @PostMapping("/webhooks")
    public ResponseEntity<Void> handlePaymentWebhook(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String stripeSignature) {
        Event event = constructEvent(payload, stripeSignature, stripeProperties.getWebhookSecret());
        if (event == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            logger.error("""
                    Deserialization failed, probably due to an API version mismatch.
                    Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
                    instructions on how to handle this case, or return an error here.""");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                logger.info("Payment for " + paymentIntent.getAmount() + " succeeded.");
            }
            case "checkout.session.completed" -> {
                logger.info("Payment succeeded");
                var session = (Session) stripeObject;
                String wallet = getWallet(session);
                new Thread(() -> web3Service.mint(wallet)).start();
            }
            default -> logger.info("Unhandled event type: " + event.getType());
        }
        return ResponseEntity.ok().build();
    }

    private static Event constructEvent(String payload, String sigHeader, String secretKey) {
        try {
            return Webhook.constructEvent(payload, sigHeader, secretKey);
        } catch (SignatureVerificationException e) {
            logger.error("Failed to construct webhook event: {}", e.getStripeError());
            return null;
        }
    }

    /**
     * Dummy method. Wallet address should be saved in the customer account
     * and retrieved here from the session object
     * @param session the session object holding the customers wallet address
     * @return the wallet address of the customer
     */
    private String getWallet(Session session) {
        return "archway1rnhrmtgpvftlx6z69c5qmtzy2te44huvnwa8xe";
    }

    /**
     * Dummy method, price id should be fetched from Stripe
     * For testing purpose enter the price id of the Product inserted into Stripe.
     * @param collectionId Unique identifier of the collection to fetch the price id from
     * @return the price id
     */
    private String getPriceId(String collectionId) {
        return "price_1Mzx2uKmEEAqXREHXorxtP1I";
    }
}
