package com.evangeliakostop.paymentsystem.integrations.stripe;

import com.evangeliakostop.paymentsystem.TestHelper;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StripeIntegrationTest {

    @InjectMocks private StripeIntegration stripeIntegration;

    @Mock private ObjectMapper objectMapper;

    @Mock private CloseableHttpClient httpClient;

    @BeforeEach
    void setUp() {
        stripeIntegration = new StripeIntegration(
                "sk_test_123",                                  // stripeSecretKey
                "http://localhost:9999/init",                   // stripeInitUrl
                "http://localhost:9999/payment_intents/{id}/confirm", // stripeConfirmUrl (με {id})
                httpClient,
                objectMapper
        );
    }


    @Test
    void initPayment_Success() throws IOException {

        String jsonFilePath = "src/test/resources/StripeResponse_init_status_succeeded.json";
        PaymentIntentDto paymentIntentDto = TestHelper.readPaymentIntentFromFile(jsonFilePath);

        PaymentRequest request = new PaymentRequest();
        request.setAmount(4L);
        request.setCurrency("usd");
        request.setPaymentType("card");

        ResponseEntity<PaymentIntentDto> output = ResponseEntity.ok().body(paymentIntentDto);

        when(httpClient.execute(
                any(HttpPost.class),
                any(HttpClientResponseHandler.class))
        ).thenReturn(paymentIntentDto);

        PaymentIntentDto response = stripeIntegration.initPayment(request, "txn");

        assertEquals(paymentIntentDto, response);
    }

    @Test
    void initPayment_Success_Null() throws IOException {

        PaymentRequest request = new PaymentRequest();
        request.setAmount(4L);
        request.setCurrency("usd");
        request.setPaymentType("card");

        when(httpClient.execute(
                any(HttpPost.class),
                any(HttpClientResponseHandler.class))
        ).thenReturn(null);

        assertThrows(CustomException.class, () -> stripeIntegration.initPayment(request, "txn"));
    }

    @Test
    void confirmIntent_success() throws IOException {
        String jsonFilePath = "src/test/resources/StripeResponse_Confirm.json";
        PaymentIntentDto mockedResponse = TestHelper.readPaymentIntentFromFile(jsonFilePath);

        when(httpClient.execute(
                any(HttpPost.class),
                any(HttpClientResponseHandler.class))
        ).thenReturn(mockedResponse);

        PaymentIntentDto request = new PaymentIntentDto();
        request.setId(mockedResponse.getId()); // just needs ID to build the URL

        PaymentIntentDto response = stripeIntegration.confirmIntent(request);

        assertEquals(mockedResponse, response);
    }
}