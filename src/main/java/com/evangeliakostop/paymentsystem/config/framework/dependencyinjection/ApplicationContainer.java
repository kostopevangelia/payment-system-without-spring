package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.config.framework.config.ApplicationConfig;
import com.evangeliakostop.paymentsystem.config.jdbc.JdbcTemplateConfig;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class ApplicationContainer {

    PaymentSystemContainer paymentSystemContainer;
    StripeSystemContainer stripeSystemContainer;
    FraudSystemContainer fraudSystemContainer;

    public ApplicationContainer() throws IOException {

        ApplicationConfig config = ApplicationConfig.load();
        JdbcTemplate jdbcTemplate = JdbcTemplateConfig.createJdbcTemplate(config);
        PaymentsDBAccess paymentsDBAccess = new PaymentsDBAccess(jdbcTemplate);

        RestTemplate restTemplateStripe = new RestTemplate();
        RestTemplate restTemplateFraudApi = new RestTemplate();

        StripeSystemContainer stripeContainer = new StripeSystemContainer(
                config.getStripeSecretKey(),
                config.getStripeInitUrl(),
                config.getStripeConfirmUrl(),
                restTemplateStripe);

        FraudIntegrationContainer fraudIntegrationContainer = new FraudIntegrationContainer(
                config.getFraudApiUrl(),
                config.getFraudApiSecretKey(),
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
