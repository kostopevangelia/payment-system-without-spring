package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.stripe.StripeIntegration;
import com.evangeliakostop.paymentsystem.models.FraudPrediction;
import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentRequest;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import com.evangeliakostop.paymentsystem.persistence.PaymentsDBAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentService {

    private final StripeIntegration stripe;
    private final PaymentsDBAccess paymentsDBAccess;
    private final FraudService fraudService;

    @Autowired
    public PaymentService(StripeIntegration stripe, PaymentsDBAccess paymentsDBAccess, FraudService fraudService) {
        this.stripe = stripe;
        this.paymentsDBAccess = paymentsDBAccess;
        this.fraudService = fraudService;
    }

    /**
     * Initiate Payment Service.
     *
     * @param request PaymentRequest
     * @return PaymentInfo
     */
    public PaymentResponse initiatePayment(PaymentRequest request, String transactionId) {

        try {
            /* PaymentIntent */
            PaymentIntentDto paymentIntent = stripe.initPayment(request, transactionId);
            paymentIntent.setTimestamp(request.getTimestamp());
            FraudPrediction fraudPrediction = fraudService.getFraudScore(paymentIntent, transactionId);

            /* If status is requires_payment_method, then call /confirm */
            if (!fraudPrediction.isFraud() && paymentIntent.getStatus().equals(PaymentStatus.REQUIRES_PAYMENT_METHOD.getDescription())) {
                paymentIntent = stripe.confirmIntent(paymentIntent);
            }
            paymentsDBAccess.insertInitTransaction(transactionId, request.getTransactionType(), request.getAmount(), request.getCurrency());

            return createClientResponse(paymentIntent, fraudPrediction, transactionId);

        } catch (DataAccessException e) {
            log.error("Method initiatePayment - Exception: {}", e.getMessage());
            throw new CustomException(
                    "PaymentService - error",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        } catch (Exception e) {
            log.error("Method initiatePayment - Exception: {}", e.getMessage());
            throw new CustomException(
                    "PaymentService - error",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR
            );

        }
    }

    private PaymentResponse createClientResponse(PaymentIntentDto paymentIntentDto, FraudPrediction fraudPrediction, String transactionId) {

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .client_secret(paymentIntentDto.getClientSecret())
                .transactionId(transactionId)
                .amount(String.valueOf(paymentIntentDto.getAmount()))
                .currency(paymentIntentDto.getCurrency())
                .paymentType("card")
                .status(paymentIntentDto.getStatus())
                .isFraud(fraudPrediction.isFraud())
                .fraudScore(fraudPrediction.isFraud() ? fraudPrediction.getFraudScore() : 0.0)
                .message(paymentIntentDto.getDescription())
                .build();
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentInfo(paymentInfo);

        return paymentResponse;
    }
}
