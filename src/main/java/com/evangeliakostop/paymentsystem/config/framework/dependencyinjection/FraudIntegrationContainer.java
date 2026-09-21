package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.integrations.FraudApiIntegration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.web.client.RestTemplate;

public class FraudIntegrationContainer {

    private final String fraudApiUrl;
    private final String fraudSecretKey;

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FraudIntegrationContainer(String fraudApiUrl,
                                     String fraudSecretKey,
                                     CloseableHttpClient httpClient,
                                     ObjectMapper objectMapper) {

        this.fraudApiUrl = fraudApiUrl;
        this.fraudSecretKey = fraudSecretKey;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public FraudApiIntegration fraudApiIntegration() {
        return new FraudApiIntegration(fraudApiUrl, fraudSecretKey, httpClient, objectMapper);
    }
}
