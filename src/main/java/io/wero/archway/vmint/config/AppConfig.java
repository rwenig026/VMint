package io.wero.archway.vmint.config;

import com.stripe.Stripe;
import io.wero.archway.vmint.config.properties.StripeProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({StripeProperties.class})
public class AppConfig {
    private final StripeProperties stripeProperties;
    private final String domain;

    public AppConfig(StripeProperties stripeProperties,
                     @Value("${app.domain:http://localhost:8080/}") String domain) {
        Stripe.apiKey = stripeProperties.getSecretKey();
        this.stripeProperties = stripeProperties;
        this.domain = domain;
    }

    /**
     * Unsafe CORS handling
     * Just for the sake of simplicity at development.
     * Add proper cors handling before going into production.
     * @return cors configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    public StripeProperties getStripeProperties() {
        return stripeProperties;
    }

    public String getDomain() {
        return domain;
    }
}
