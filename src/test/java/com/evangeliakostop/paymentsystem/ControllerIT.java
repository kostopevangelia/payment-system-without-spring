package com.evangeliakostop.paymentsystem;

import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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
        reset(ControllerTestInitializer.restTemplateStripe);
    }

    @Test
    void completePayment_Success() throws Exception {

        RestTemplate restTemplateStripe =
                ControllerTestInitializer.restTemplateStripe;

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

        when(restTemplateStripe.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentIntentDto.class)
        )).thenReturn(
                new ResponseEntity<>(paymentIntent, HttpStatus.OK)
        );

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

        RestTemplate restTemplateStripe =
                ControllerTestInitializer.restTemplateStripe;

        PaymentRequest request =
                TestHelper.parseJsonToPaymentRequest(
                        "src/test/resources/PaymentRequest_Invalid.json");

        when(restTemplateStripe.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentIntentDto.class)
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

        RestTemplate restTemplateStripe =
                ControllerTestInitializer.restTemplateStripe;

        PaymentRequest request =
                TestHelper.parseJsonToPaymentRequest(
                        "src/test/resources/PaymentRequest_Invalid.json");

        PaymentIntentDto paymentIntent = new PaymentIntentDto();
        paymentIntent.setId("pi_test_123");
        paymentIntent.setAmount(Math.toIntExact(request.getAmount()));
        paymentIntent.setCurrency(request.getCurrency());
        paymentIntent.setStatus("requires_payment_method");

        when(restTemplateStripe.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentIntentDto.class)
        )).thenReturn(
                new ResponseEntity<>(paymentIntent, HttpStatus.OK)
        );

        when(restTemplateStripe.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentIntentDto.class),
                anyMap()
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