package com.evangeliakostop.paymentsystem.services;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.ErrorLevelEnum;
import com.evangeliakostop.paymentsystem.dto.FraudApiRequest;
import com.evangeliakostop.paymentsystem.dto.FraudDto;
import com.evangeliakostop.paymentsystem.dto.PaymentIntentDto;
import com.evangeliakostop.paymentsystem.exceptions.CustomException;
import com.evangeliakostop.paymentsystem.integrations.FraudApiIntegration;
import com.evangeliakostop.paymentsystem.models.FraudPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class FraudService {

    private final FraudApiIntegration fraudIntegration;

    public FraudService(FraudApiIntegration fraudIntegration) {
        this.fraudIntegration = fraudIntegration;
    }

    public FraudPrediction getFraudScore(PaymentIntentDto paymentIntent, String transactionId) throws Exception {

        FraudDto fraudDto;

        try {
            fraudDto = fraudIntegration.predictFraud(prepareFraudRequest(paymentIntent), transactionId);
            return FraudPrediction.builder()
                    .isFraud(fraudDto.isFraud())
                    .fraudScore(fraudDto.getFraudScore())
                    .build();
        } catch (Exception e) {
            throw new CustomException(
                    "Error occurred in service FraudService and method getFraudScore",
                    e.getMessage(),
                    transactionId,
                    ErrorLevelEnum.APPLICATION_ERROR
            );
        }
    }

    private FraudApiRequest prepareFraudRequest(PaymentIntentDto paymentIntent) {
        return FraudApiRequest.builder()
                .amount(paymentIntent.getAmount())
                .currency(paymentIntent.getCurrency().toUpperCase(Locale.ROOT))
                .paymentType("card")
                .transactionType("PAYMENT")
                .userId(paymentIntent.getId())
                .bin(paymentIntent.getCard() != null && paymentIntent.getCard().length() >= 6
                        ? paymentIntent.getCard().substring(0, 6)
                        : "000000")
                .hour(paymentIntent.getTimestamp().getHour())
                .dayOfWeek(paymentIntent.getTimestamp().getDayOfWeek().getValue() - 1)
                .build();
    }
}
