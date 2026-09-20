package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApplicationContainer {

    PaymentSystemContainer paymentSystemContainer;
    StripeSystemContainer stripeSystemContainer;
    FraudSystemContainer fraudSystemContainer;

    public ApplicationContainer(@Value("${stripe.secret.key}") String stripeSecretKey,
                                @Value("${stripe.init.url}") String stripeInitUrl,
                                @Value("${stripe.confirm.url}") String stripeConfirmUrl,
                                @Qualifier("restTemplateStripe") RestTemplate restTemplateStripe,
                                @Value("${fraud.api.url}") String fraudApiUrl,
                                @Value("${fraud.api.secret.key}") String fraudSecretKey,
                                RestTemplate restTemplateFraudApi,
                                PaymentsDBAccess paymentsDBAccess) {

        StripeSystemContainer stripeContainer = new StripeSystemContainer(
                stripeSecretKey,
                stripeInitUrl,
                stripeConfirmUrl,
                restTemplateStripe);

        FraudIntegrationContainer fraudIntegrationContainer = new FraudIntegrationContainer(
                fraudApiUrl,
                fraudSecretKey,
                restTemplateFraudApi);

        FraudSystemContainer fraudContainer = new FraudSystemContainer(
                fraudIntegrationContainer);

        PaymentSystemContainer paymentContainer = new PaymentSystemContainer(
                stripeContainer,
                paymentsDBAccess,
                fraudContainer);

        this.stripeSystemContainer = stripeContainer;
        this.fraudSystemContainer = fraudContainer;
        this.paymentSystemContainer = paymentContainer;
    }

    public PaymentService paymentService() {
        return paymentSystemContainer.paymentService();
    }
}
