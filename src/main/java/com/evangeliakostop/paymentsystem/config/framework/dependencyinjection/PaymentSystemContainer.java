package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import com.evangeliakostop.paymentsystem.services.FraudService;
import com.evangeliakostop.paymentsystem.services.PaymentService;

public class PaymentSystemContainer {

    private final StripeSystemContainer stripeSystemContainer;
    private final PaymentsDBAccess paymentsDBAccess;
    private final FraudSystemContainer fraudSystemContainer;

    public PaymentSystemContainer(StripeSystemContainer stripeSystemContainer,
                                  PaymentsDBAccess paymentsDBAccess,
                                  FraudSystemContainer fraudSystemContainer) {

        this.stripeSystemContainer = stripeSystemContainer;
        this.paymentsDBAccess = paymentsDBAccess;
        this.fraudSystemContainer = fraudSystemContainer;
    }

    public PaymentService paymentService() {
        StripeIntegration stripe = stripeSystemContainer.stripeIntegration();
        FraudService fraudService = fraudSystemContainer.fraudService();
        return new PaymentService(stripe, paymentsDBAccess, fraudService);
    }
}
