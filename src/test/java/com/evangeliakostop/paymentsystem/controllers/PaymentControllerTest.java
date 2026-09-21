package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.config.PaymentHttpStatusResolver;
import com.evangeliakostop.paymentsystem.config.framework.dependencyinjection.ApplicationContainer;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock private PaymentService paymentService;

    private ObjectMapper objectMapper;

    private PaymentController controller;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = JsonMapper.builder()
                .build();
    }

    @Test
    void completePayment_Success() throws Exception {
        String jsonRequest = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonRequest);

        String jsonResponse = "src/test/resources/PaymentResponse.json";
        PaymentResponse mockedResponse = TestHelper.createPaymentResponseFromJson(jsonResponse);

        String jsonPaymentResponse = "src/test/resources/PaymentResponse.json";
        PaymentResponse paymentResponse = TestHelper.createPaymentResponseFromJson(jsonPaymentResponse);

        when(paymentService.initiatePayment(any(), anyString()))
                .thenReturn(paymentResponse);

        try(MockedConstruction<ApplicationContainer> ignored =
                mockConstruction(
                        ApplicationContainer.class,
                        (mock, context) ->
                                when(mock.paymentService()).thenReturn(paymentService));

            MockedConstruction<PaymentHttpStatusResolver> ignoredResolver =
                    mockConstruction(
                            PaymentHttpStatusResolver.class,
                            (mock, context) ->
                                    when(mock.resolve(any(PaymentResponse.class)))
                                            .thenReturn(HttpStatus.OK))) {

            PaymentController controller = new PaymentController();
            ResponseEntity<PaymentResponse> response = controller.initPayment(request);

            String json = objectMapper.writeValueAsString(response);
            log.info("Response: {}", json);

            assertNotNull(response.getBody());
            assertEquals(mockedResponse.getPaymentInfo().getAmount(), response.getBody().getPaymentInfo().getAmount());
        }
    }


}