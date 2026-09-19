package com.evangeliakostop.paymentsystem.integrations.stripe;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StripeIntegrationTest {

    @Mock
    private RestTemplate restTemplateStripe;

    @InjectMocks
    private StripeIntegration stripeIntegration;

    @BeforeEach
    void setUp() {
        stripeIntegration = new StripeIntegration(
                "sk_test_123",                                  // stripeSecretKey
                "http://localhost:9999/init",                   // stripeInitUrl
                "http://localhost:9999/payment_intents/{id}/confirm", // stripeConfirmUrl (με {id})
                restTemplateStripe
        );
    }


    @Test
    void initPayment_Success() {

        String jsonFilePath = "src/test/resources/StripeResponse_init_status_succeeded.json";
        PaymentIntentDto paymentIntentDto = TestHelper.readPaymentIntentFromFile(jsonFilePath);

        PaymentRequest request = new PaymentRequest();
        request.setAmount(4L);
        request.setCurrency("usd");
        request.setPaymentType("card");

        ResponseEntity<PaymentIntentDto> output = ResponseEntity.ok().body(paymentIntentDto);
        when(restTemplateStripe.exchange(anyString(), any(HttpMethod.class), any(), eq(PaymentIntentDto.class))).thenReturn(output);

        PaymentIntentDto response = stripeIntegration.initPayment(request, "txn");

        assertEquals(paymentIntentDto, response);
    }

    @Test
    void initPayment_Success_Null() {

        PaymentRequest request = new PaymentRequest();
        request.setAmount(4L);
        request.setCurrency("usd");
        request.setPaymentType("card");

        ResponseEntity<PaymentIntentDto> output = ResponseEntity.ok().body(null);
        when(restTemplateStripe.exchange(anyString(), any(HttpMethod.class), any(), eq(PaymentIntentDto.class))).thenReturn(output);

        assertThrows(CustomException.class, () -> stripeIntegration.initPayment(request, "txn"));
    }

    @Test
    void confirmIntent_success() {
        String jsonFilePath = "src/test/resources/StripeResponse_Confirm.json";
        PaymentIntentDto mockedResponse = TestHelper.readPaymentIntentFromFile(jsonFilePath);

        ResponseEntity<PaymentIntentDto> output = ResponseEntity.ok(mockedResponse);

        when(restTemplateStripe.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(PaymentIntentDto.class),
                anyMap()
        )).thenReturn(output);

        PaymentIntentDto request = new PaymentIntentDto();
        request.setId(mockedResponse.getId()); // just needs ID to build the URL

        PaymentIntentDto response = stripeIntegration.confirmIntent(request);

        assertEquals(mockedResponse, response);
    }
}