package com.evangeliakostop.paymentsystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FraudDto {
    private double fraudScore;
    private boolean isFraud;
}