package com.evangeliakostop.paymentsystem.integrations;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.dto.FraudApiRequest;
import com.evangeliakostop.paymentsystem.dto.FraudDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

@Slf4j
public class FraudApiIntegration {

    private final String fraudApiUrl;
    private final String fraudSecretKey;

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FraudApiIntegration(String fraudApiUrl,
                               String fraudSecretKey,
                               CloseableHttpClient httpClient,
                               ObjectMapper objectMapper) {
        this.fraudApiUrl = fraudApiUrl;
        this.fraudSecretKey = fraudSecretKey;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public FraudDto predictFraud(FraudApiRequest request, String transactionId) throws JsonProcessingException {

        HttpPost httpPost = new HttpPost(fraudApiUrl);
        httpPost.setHeader("Authorization", "Bearer " + fraudSecretKey);
        httpPost.setHeader("Content-Type ", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        httpPost.setEntity(new StringEntity(
                        objectMapper.writeValueAsString(request),
                        ContentType.APPLICATION_JSON));

        try {
            return executeRequest(httpPost);
        } catch (Exception e) {
            // Handle generic exceptions
            log.error("predictFraud: FraudApi error: {}", e.getMessage());
            throw new CustomException(
                    "FraudApiIntegration - error",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        }
    }

    private FraudDto executeRequest(HttpPost httpPost) throws IOException {
        return httpClient.execute(
                httpPost,
                response -> {
                    String responseBody = EntityUtils.toString(response.getEntity());

                    if (responseBody == null || responseBody.isBlank()) {
                        throw new CustomException("Error response from FraudApi: ",
                                "Response Body cannot be null",
                                null,
                                ErrorLevelEnum.APPLICATION_ERROR);
                    }

                    return objectMapper.readValue(responseBody, FraudDto.class);
                }
        );
    }
}
