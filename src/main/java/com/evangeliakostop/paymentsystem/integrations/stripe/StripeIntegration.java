package com.evangeliakostop.paymentsystem.integrations.stripe;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StripeIntegration {

    private final String stripeSecretKey;
    private final String stripeInitUrl;
    private final String stripeConfirmUrl;

    private final RestTemplate restTemplateStripe;

    public StripeIntegration(String stripeSecretKey,
                             String stripeInitUrl,
                             String stripeConfirmUrl,
                             RestTemplate restTemplateStripe) {
        this.stripeSecretKey = stripeSecretKey;
        this.stripeInitUrl = stripeInitUrl;
        this.stripeConfirmUrl = stripeConfirmUrl;
        this.restTemplateStripe = restTemplateStripe;
    }


    /**
     * Init Payment
     *
     * @param request       the request
     * @param transactionId String
     * @return the PaymentIntentDto
     */
    public PaymentIntentDto initPayment(PaymentRequest request, String transactionId) {

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("amount", String.valueOf(request.getAmount()));
        requestParams.add("currency", request.getCurrency());
        requestParams.add("automatic_payment_methods[enabled]", "true");
        requestParams.add("automatic_payment_methods[allow_redirects]", "never");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + stripeSecretKey);  // Use your Stripe secret key here

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestParams, headers);
        ResponseEntity<PaymentIntentDto> response;

        try {
            response = restTemplateStripe.exchange(stripeInitUrl, HttpMethod.POST, entity, PaymentIntentDto.class);
            if (response.getBody() != null) {
                return response.getBody();
            } else {
                throw new CustomException(
                        "Error response from stripe:",
                        "Response Body cannot be null",
                        null,
                        ErrorLevelEnum.APPLICATION_ERROR
                );
            }
        } catch (Exception e) {
            // Handle generic exceptions
            log.error("initPayment: Stripe error: {}", e.getMessage());
            throw new CustomException(
                    "StripeIntegration - error",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        }
    }

    /**
     * Confirm Intent.
     *
     * @param paymentIntent PaymentIntentDto
     * @return PaymentIntentDto
     */
    public PaymentIntentDto confirmIntent(PaymentIntentDto paymentIntent) {

        // Prepare the request parameters
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("payment_method", "pm_card_visa");

        // Set the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + stripeSecretKey);

        // Create the request entity
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestParams, headers);

        // Prepare the URL template variables (in this case, the PaymentIntent ID)
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", paymentIntent.getId());  // Set the PaymentIntent ID

        try {
            // Perform the HTTP request and exchange the response
            ResponseEntity<PaymentIntentDto> response = restTemplateStripe.exchange(stripeConfirmUrl, HttpMethod.POST, entity, PaymentIntentDto.class, uriVariables);

            if (response.getBody() != null) {
                return response.getBody();
            } else {
                throw new CustomException(
                        "Error response from stripe:",
                        "Response Body cannot be null",
                        null,
                        ErrorLevelEnum.APPLICATION_ERROR
                );
            }
        } catch (Exception e) {
            // Handle generic exceptions
            log.error("confirmIntent: Stripe error: {}", e.getMessage());
            throw new CustomException(
                    "StripeIntegration - error",
                    e.getMessage(),
                    null,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        }
    }

}
