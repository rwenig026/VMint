package io.wero.archway.fdrop.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {
    private String secretKey;
    private String webhookSecret;

    public String getSecretKey() {
        return secretKey;
    }

    public StripeProperties setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public StripeProperties setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
        return this;
    }
}
