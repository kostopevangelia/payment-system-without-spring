package com.evangeliakostop.paymentsystem.integrations;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.dto.FraudApiRequest;
import com.evangeliakostop.paymentsystem.dto.FraudDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class FraudApiIntegration {

    private final String fraudApiUrl;
    private final String fraudSecretKey;
    private final RestTemplate restTemplateFraudApi;

    public FraudApiIntegration(@Value("${fraud.api.url}") String fraudApiUrl,
                               @Value("${fraud.api.secret.key}") String fraudSecretKey,
                               RestTemplate restTemplateFraudApi) {
        this.fraudApiUrl = fraudApiUrl;
        this.fraudSecretKey = fraudSecretKey;
        this.restTemplateFraudApi = restTemplateFraudApi;
    }

    public FraudDto predictFraud(FraudApiRequest request, String transactionId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", "Bearer " + fraudSecretKey);

        HttpEntity<FraudApiRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<FraudDto> response = null;

        try {
            response = restTemplateFraudApi.exchange(fraudApiUrl, HttpMethod.POST, entity, FraudDto.class);
            if (response.getBody() != null) {
                return response.getBody();
            } else {
                throw new CustomException(
                        "Error response from fraud api:",
                        "Response Body cannot be null",
                        null,
                        ErrorLevelEnum.APPLICATION_ERROR
                );
            }
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
}
