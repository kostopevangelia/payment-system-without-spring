package com.evangeliakostop.paymentsystem;

import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerIT {

    private static final int STRIPE_PORT = 18080;

    private static HttpServer stripeServer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void startStripeServer() throws IOException {

        stripeServer = HttpServer.create(
                new InetSocketAddress("localhost", STRIPE_PORT),
                0
        );

        stripeServer.createContext("/stripe/init", exchange -> {

            String response = """
                    {
                      "id": "pi_test_123",
                      "amount": 100,
                      "currency": "USD",
                      "status": "succeeded"
                    }
                    """;

            sendResponse(exchange, 200, response);
        });

        stripeServer.createContext("/stripe/confirm", exchange -> {

            String response = """
                    {
                      "id": "pi_test_123",
                      "amount": 100,
                      "currency": "USD",
                      "status": "succeeded"
                    }
                    """;

            sendResponse(exchange, 200, response);
        });

        stripeServer.start();
    }

    @AfterAll
    static void stopStripeServer() {

        if (stripeServer != null) {
            stripeServer.stop(0);
        }
    }

    @Test
    void completePayment_Success() throws Exception {

        String jsonRequest =
                "src/test/resources/PaymentRequest.json";

        PaymentRequest request =
                TestHelper.parseJsonToPaymentRequest(jsonRequest);

        String jsonResponse =
                "src/test/resources/PaymentResponse.json";

        PaymentResponse expectedResponse =
                TestHelper.createPaymentResponseFromJson(jsonResponse);

        String response = mockMvc.perform(
                        post("/payments/init")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentResponse actualResponse =
                objectMapper.readValue(response, PaymentResponse.class);

        assertEquals(
                expectedResponse.getPaymentInfo().getAmount(),
                actualResponse.getPaymentInfo().getAmount()
        );
    }

    @Test
    void completePayment_InitException() throws Exception {

        String jsonRequest =
                "src/test/resources/PaymentRequest_Invalid.json";

        PaymentRequest request =
                TestHelper.parseJsonToPaymentRequest(jsonRequest);

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

        String jsonRequest =
                "src/test/resources/PaymentRequest_Invalid.json";

        PaymentRequest request =
                TestHelper.parseJsonToPaymentRequest(jsonRequest);

        mockMvc.perform(
                        post("/payments/init")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isInternalServerError());
    }

    private static void sendResponse(
            com.sun.net.httpserver.HttpExchange exchange,
            int status,
            String response) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }
}