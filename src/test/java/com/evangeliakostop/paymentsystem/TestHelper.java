package com.evangeliakostop.paymentsystem;

import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stripe.model.PaymentIntent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()); // Register Java Time module

    public static PaymentIntentDto convertToPaymentDto(PaymentIntent paymentIntent) {
        PaymentIntentDto paymentIntentDto = new PaymentIntentDto();
        paymentIntentDto.setId(paymentIntent.getId());
        paymentIntentDto.setAmount(Math.toIntExact(paymentIntent.getAmount()));
        paymentIntentDto.setCurrency(paymentIntent.getCurrency());
        paymentIntentDto.setCustomer(paymentIntent.getCustomer());
        paymentIntentDto.setDescription(paymentIntent.getDescription());
        paymentIntentDto.setPaymentMethod(paymentIntent.getPaymentMethod());
        paymentIntentDto.setStatus(paymentIntent.getStatus());
        return paymentIntentDto;
    }

    public static PaymentIntentDto readPaymentIntentFromFile(String filePath) {

        try {
            return objectMapper.readValue(new File(filePath), PaymentIntentDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read PaymentIntentDto from file", e);
        }
    }

    public static PaymentInfo createPaymentInfoFromJson(String jsonFilePath) {
        try {
            // Read JSON as string
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            // Deserialize JSON into PaymentRequest
            return objectMapper.readValue(jsonContent, PaymentInfo.class);

        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage(), e);
        }
    }

    public static PaymentResponse createPaymentResponseFromJson(String jsonFilePath) {
        try {
            // Read JSON as string
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            // Deserialize JSON into PaymentRequest
            return objectMapper.readValue(jsonContent, PaymentResponse.class);

        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage(), e);
        }
    }

    public static PaymentRequest parseJsonToPaymentRequest(String jsonFilePath) {
        try {
            // Read JSON file content
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            // Deserialize JSON into PaymentRequest object
            return objectMapper.readValue(jsonContent, PaymentRequest.class);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage(), e);
        }
    }


    public static PaymentIntentDto createPaymentIntentDTOFromJson(String jsonFilePath) {
        try {
            // Read JSON as string
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            // Deserialize JSON into PaymentRequest
            return objectMapper.readValue(jsonContent, PaymentIntentDto.class);

        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage(), e);
        }
    }
}
