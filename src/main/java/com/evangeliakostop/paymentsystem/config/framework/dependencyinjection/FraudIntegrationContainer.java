package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.integrations.FraudApiIntegration;
import org.springframework.web.client.RestTemplate;

public class FraudIntegrationContainer {

    private final String fraudApiUrl;
    private final String fraudSecretKey;
    private final RestTemplate restTemplateFraudApi;

    public FraudIntegrationContainer(String fraudApiUrl,
                                     String fraudSecretKey,
                                     RestTemplate restTemplateFraudApi) {
        this.fraudApiUrl = fraudApiUrl;
        this.fraudSecretKey = fraudSecretKey;
        this.restTemplateFraudApi = restTemplateFraudApi;
    }

    public FraudApiIntegration fraudApiIntegration() {
        return new FraudApiIntegration(fraudApiUrl, fraudSecretKey, restTemplateFraudApi);
    }
}
