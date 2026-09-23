package com.evangeliakostop.paymentsystem.config.framework.dependencyinjection;

import com.evangeliakostop.paymentsystem.config.framework.config.ApplicationConfig;
import com.evangeliakostop.paymentsystem.config.jdbc.JdbcTemplateConfig;
import com.evangeliakostop.paymentsystem.config.rest.CorrelationIdInterceptor;
import com.evangeliakostop.paymentsystem.config.rest.RestConfig;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

public class ApplicationContainer {

    PaymentSystemContainer paymentSystemContainer;
    StripeSystemContainer stripeSystemContainer;
    FraudSystemContainer fraudSystemContainer;

    public ApplicationContainer() throws IOException {

        ApplicationConfig config = ApplicationConfig.load();
        JdbcTemplate jdbcTemplate = JdbcTemplateConfig.createJdbcTemplate(config);
        PaymentsDBAccess paymentsDBAccess = new PaymentsDBAccess(jdbcTemplate);

        CorrelationIdInterceptor correlationIdInterceptor =
                new CorrelationIdInterceptor();

        RestConfig restConfig = new RestConfig(correlationIdInterceptor);

        RequestConfig requestConfig = restConfig.createRequestConfig();
        CloseableHttpClient httpClient = restConfig.createHttpClient(requestConfig);

        ObjectMapper objectMapper = new ObjectMapper();

        StripeSystemContainer stripeContainer = new StripeSystemContainer(
                config.getStripeSecretKey(),
                config.getStripeInitUrl(),
                config.getStripeConfirmUrl(),
                httpClient,
                objectMapper);

        FraudIntegrationContainer fraudIntegrationContainer = new FraudIntegrationContainer(
                config.getFraudApiUrl(),
                config.getFraudApiSecretKey(),
                httpClient,
                objectMapper);

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
