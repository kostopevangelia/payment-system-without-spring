package com.evangeliakostop.paymentsystem.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FraudPrediction {
    private boolean isFraud;
    private double fraudScore;
}
