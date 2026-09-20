package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import org.springframework.web.client.RestTemplate;

public class StripeSystemContainer {

    private final String stripeSecretKey;
    private final String stripeInitUrl;
    private final String stripeConfirmUrl;

    private final RestTemplate restTemplateStripe;

    public StripeSystemContainer(String stripeSecretKey,
                                 String stripeInitUrl,
                                 String stripeConfirmUrl,
                                 RestTemplate restTemplateStripe) {

        this.stripeSecretKey = stripeSecretKey;
        this.stripeInitUrl = stripeInitUrl;
        this.stripeConfirmUrl = stripeConfirmUrl;
        this.restTemplateStripe = restTemplateStripe;
    }

    public StripeIntegration stripeIntegration() {
        return new StripeIntegration(stripeSecretKey, stripeInitUrl, stripeConfirmUrl, restTemplateStripe);
    }
}
