package com.evangeliakostop.paymentsystem.controllers;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.common.utils.CommonService;
import com.evangeliakostop.paymentsystem.config.PaymentHttpStatusResolver;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.FraudPrediction;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.services.FraudService;
import com.evangeliakostop.paymentsystem.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;
    @Mock
    private FraudService fraudService;
    @Mock
    private StripeIntegration stripe;
    @Mock
    private PaymentHttpStatusResolver paymentHttpStatusResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentController controller;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .build();
    }

    @Test
    void completePayment_Success() throws Exception {
        String jsonRequest = "src/test/resources/PaymentRequest.json";
        PaymentRequest request = TestHelper.parseJsonToPaymentRequest(jsonRequest);

        String jsonResponse = "src/test/resources/PaymentResponse.json";
        PaymentResponse mockedResponse = TestHelper.createPaymentResponseFromJson(jsonResponse);

        String jsonPaymentIntent = "src/test/resources/PaymentIntentDTO.json";
        PaymentIntentDto paymentIntentDto = TestHelper.createPaymentIntentDTOFromJson(jsonPaymentIntent);

        String jsonPaymentIntentConfirmed = "src/test/resources/StripeResponse_Confirm.json";
        PaymentIntentDto paymentIntentConfirmed = TestHelper.createPaymentIntentDTOFromJson(jsonPaymentIntentConfirmed);

        String jsonPaymentResponse = "src/test/resources/PaymentResponse.json";
        PaymentResponse paymentResponse = TestHelper.createPaymentResponseFromJson(jsonPaymentResponse);

        when(stripe.initPayment(any(), anyString())).thenReturn(paymentIntentDto);
        when(fraudService.getFraudScore(any(), anyString())).thenReturn(FraudPrediction.builder().isFraud(false).fraudScore(1.2).build());
        when(stripe.confirmIntent(any())).thenReturn(paymentIntentConfirmed);

        when(paymentService.initiatePayment(any(), anyString())).thenReturn(paymentResponse);
        when(paymentHttpStatusResolver.resolve(any(PaymentResponse.class)))
                .thenReturn(HttpStatus.OK);

        ResponseEntity<PaymentResponse> response = controller.initPayment(request);

        String json = objectMapper.writeValueAsString(response);
        log.info("Response: {}", json);

        assertNotNull(response.getBody());
        assertEquals(mockedResponse.getPaymentInfo().getAmount(), response.getBody().getPaymentInfo().getAmount());

    }


}