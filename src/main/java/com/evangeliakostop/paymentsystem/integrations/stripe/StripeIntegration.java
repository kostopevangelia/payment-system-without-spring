package com.evangeliakostop.paymentsystem.integrations.stripe;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StripeIntegration {

    private final String stripeSecretKey;
    private final String stripeInitUrl;
    private final String stripeConfirmUrl;

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public StripeIntegration(String stripeSecretKey,
                             String stripeInitUrl,
                             String stripeConfirmUrl,
                             CloseableHttpClient httpClient,
                             ObjectMapper objectMapper) {
        this.stripeSecretKey = stripeSecretKey;
        this.stripeInitUrl = stripeInitUrl;
        this.stripeConfirmUrl = stripeConfirmUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }


    /**
     * Init Payment
     *
     * @param request       the request
     * @param transactionId String
     * @return the PaymentIntentDto
     */
    public PaymentIntentDto initPayment(PaymentRequest request, String transactionId) {

        List<NameValuePair> requestParams = new ArrayList<>();
        requestParams.add(new BasicNameValuePair("amount", String.valueOf(request.getAmount())));
        requestParams.add(new BasicNameValuePair("currency", request.getCurrency()));
        requestParams.add(new BasicNameValuePair("automatic_payment_methods[enabled]", "true"));
        requestParams.add(new BasicNameValuePair("automatic_payment_methods[allow_redirects]", "never"));

        HttpPost httpPost = new HttpPost(stripeInitUrl);
        httpPost.setHeader("Authorization", "Bearer " + stripeSecretKey);
        httpPost.setHeader("Content-Type ", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        httpPost.setEntity(new UrlEncodedFormEntity(requestParams));

        try {
            return executeRequest(httpPost);
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
        List<NameValuePair> requestParams = new ArrayList<>();
        requestParams.add(new BasicNameValuePair("payment_method", "pm_card_visa"));

        // Set the HTTP headers
        String url = stripeConfirmUrl.replace("{id}", paymentIntent.getId());
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Bearer " + stripeSecretKey);
        httpPost.setHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        httpPost.setEntity(new UrlEncodedFormEntity(requestParams));

        try {
            return executeRequest(httpPost);
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

    private PaymentIntentDto executeRequest(HttpPost httpPost) throws IOException {

        return httpClient.execute(
                httpPost,
                response -> {
                    String responseBody = EntityUtils.toString(response.getEntity());

                    if (responseBody == null || responseBody.isBlank()) {
                        throw new CustomException("Error response from Stripe: ",
                                "Response Body cannot be null",
                                null,
                                ErrorLevelEnum.APPLICATION_ERROR);
                    }

                    return objectMapper.readValue(responseBody, PaymentIntentDto.class);

                }
        );
    }
}
