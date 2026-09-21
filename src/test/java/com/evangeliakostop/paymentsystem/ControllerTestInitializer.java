package com.evangeliakostop.paymentsystem;

import com.evangeliakostop.paymentsystem.config.framework.dependencyinjection.ApplicationContainer;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.FraudPrediction;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import com.evangeliakostop.paymentsystem.services.FraudService;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class ControllerTestInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static CloseableHttpClient httpClientStripe;
    public static ObjectMapper objectMapper;

    public static PaymentsDBAccess paymentsDBAccess;
    public static FraudService fraudService;

    private static PaymentService paymentService;
    private static MockedConstruction<ApplicationContainer> applicationContainerMock;

    @Override
    public synchronized void initialize(
            ConfigurableApplicationContext applicationContext) {

        if (applicationContainerMock != null) {
            return;
        }

        httpClientStripe = mock(CloseableHttpClient.class);
        paymentsDBAccess = mock(PaymentsDBAccess.class);
        fraudService = mock(FraudService.class);

        FraudPrediction fraudPrediction = mock(FraudPrediction.class);

        when(fraudPrediction.isFraud()).thenReturn(false);

        try {
            when(fraudService.getFraudScore(
                    any(PaymentIntentDto.class),
                    anyString()
            )).thenReturn(fraudPrediction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StripeIntegration stripeIntegration =
                new StripeIntegration(
                        "test-stripe-secret",
                        "http://test/stripe/init",
                        "http://test/stripe/confirm",
                        httpClientStripe,
                        objectMapper
                );

        paymentService =
                new PaymentService(
                        stripeIntegration,
                        paymentsDBAccess,
                        fraudService
                );

        applicationContainerMock =
                mockConstruction(
                        ApplicationContainer.class,
                        (mock, context) ->
                                when(mock.paymentService())
                                        .thenReturn(paymentService)
                );

        applicationContext.addApplicationListener(event -> {

            if (event instanceof ContextClosedEvent) {

                if (applicationContainerMock != null) {
                    applicationContainerMock.close();
                    applicationContainerMock = null;
                    paymentService = null;
                    httpClientStripe = null;
                    objectMapper = null;
                    paymentsDBAccess = null;
                    fraudService = null;
                }
            }
        });
    }
}

