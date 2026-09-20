package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.integrations.FraudApiIntegration;
import com.evangeliakostop.paymentsystem.services.FraudService;

public class FraudSystemContainer {

    private final FraudIntegrationContainer fraudIntegrationContainer;

    public FraudSystemContainer(FraudIntegrationContainer fraudIntegrationContainer) {
        this.fraudIntegrationContainer = fraudIntegrationContainer;
    }

    public FraudService fraudService() {
        FraudApiIntegration fraudIntegration = fraudIntegrationContainer.fraudApiIntegration();
        return new FraudService(fraudIntegration);
    }
}
