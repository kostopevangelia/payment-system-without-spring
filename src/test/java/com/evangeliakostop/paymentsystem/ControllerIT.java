package com.evangeliakostop.paymentsystem;

import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ControllerTestInitializer.class)
public class ControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetStripeMock() {
        reset(ControllerTestInitializer.httpClientStripe);
    }

    @Test
    void completePayment_Success() throws Exception {

        CloseableHttpClient httpClientStripe =
                ControllerTestInitializer.httpClientStripe;

        PaymentRequest request =
                TestHelper.parseJsonToPaymentRequest(
                        "src/test/resources/PaymentRequest.json");

        PaymentResponse expectedResponse =
                TestHelper.createPaymentResponseFromJson(
                        "src/test/resources/PaymentResponse.json");

        PaymentIntentDto paymentIntent = new PaymentIntentDto();
        paymentIntent.setId("pi_test_123");
        paymentIntent.setAmount(Math.toIntExact(request.getAmount()));
        paymentIntent.setCurrency(request.getCurrency());
        paymentIntent.setStatus("succeeded");

        when(httpClientStripe.execute(
                any(HttpPost.class),
                any(HttpClientResponseHandler.class)
        )).thenReturn(paymentIntent);

        MvcResult result = mockMvc.perform(
                        post("/payments/init")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponse response =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        PaymentResponse.class
                );

        assertEquals(
                expectedResponse.getPaymentInfo().getAmount(),
                response.getPaymentInfo().getAmount()
        );
    }

    @Test
    void completePayment_InitException() throws Exception {

        CloseableHttpClient httpClientStripe =
                ControllerTestInitializer.httpClientStripe;

        PaymentRequest request =
                TestHelper.parseJsonToPaymentRequest(
                        "src/test/resources/PaymentRequest_Invalid.json");

        when(httpClientStripe.execute(
                any(HttpPost.class),
                any(HttpClientResponseHandler.class)
        )).thenThrow(
                new RuntimeException("Stripe init error")
        );

        mockMvc.perform(
                        post("/payments/init")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    void completePayment_ConfirmException() throws Exception {

        CloseableHttpClient httpClientStripe =
                ControllerTestInitializer.httpClientStripe;

        PaymentRequest request =
                TestHelper.parseJsonToPaymentRequest(
                        "src/test/resources/PaymentRequest_Invalid.json");

        PaymentIntentDto paymentIntent = new PaymentIntentDto();
        paymentIntent.setId("pi_test_123");
        paymentIntent.setAmount(Math.toIntExact(request.getAmount()));
        paymentIntent.setCurrency(request.getCurrency());
        paymentIntent.setStatus("requires_payment_method");

        // Stripe init succeeds
        when(httpClientStripe.execute(
                argThat(httpPost ->
                        httpPost != null && httpPost.getRequestUri()
                                .toString()
                                .contains("/stripe/init")),
                any(HttpClientResponseHandler.class)
        )).thenReturn(paymentIntent);

        // Stripe confirm fails
        when(httpClientStripe.execute(
                argThat(httpPost ->
                        httpPost != null && httpPost.getRequestUri()
                                .toString()
                                .contains("/stripe/confirm")),
                any(HttpClientResponseHandler.class)
        )).thenThrow(
                new RuntimeException("Stripe confirm error")
        );

        mockMvc.perform(
                        post("/payments/init")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isInternalServerError());
    }
}