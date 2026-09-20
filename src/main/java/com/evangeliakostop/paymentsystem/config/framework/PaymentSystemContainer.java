package com.evangeliakostop.paymentsystem.config.framework;

import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import com.evangeliakostop.paymentsystem.services.FraudService;
import com.evangeliakostop.paymentsystem.services.PaymentService;

public class PaymentSystemContainer {

    private final StripeIntegration stripe;
    private final PaymentsDBAccess paymentsDBAccess;
    private final FraudService fraudService;

    public PaymentSystemContainer(StripeIntegration stripe,
                                  PaymentsDBAccess paymentsDBAccess,
                                  FraudService fraudService) {
        this.stripe = stripe;
        this.paymentsDBAccess = paymentsDBAccess;
        this.fraudService = fraudService;
    }

    public PaymentService paymentService() {
        return new PaymentService(stripe, paymentsDBAccess, fraudService);
    }
}
