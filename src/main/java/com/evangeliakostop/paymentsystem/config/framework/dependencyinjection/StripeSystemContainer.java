package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.web.client.RestTemplate;

public class StripeSystemContainer {

    private final String stripeSecretKey;
    private final String stripeInitUrl;
    private final String stripeConfirmUrl;

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public StripeSystemContainer(String stripeSecretKey,
                                 String stripeInitUrl,
                                 String stripeConfirmUrl,
                                 CloseableHttpClient httpClient,
                                 ObjectMapper objectMapper) {

        this.stripeSecretKey = stripeSecretKey;
        this.stripeInitUrl = stripeInitUrl;
        this.stripeConfirmUrl = stripeConfirmUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public StripeIntegration stripeIntegration() {
        return new StripeIntegration(stripeSecretKey, stripeInitUrl, stripeConfirmUrl, httpClient, objectMapper);
    }
}
