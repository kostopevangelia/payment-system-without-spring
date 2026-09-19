package com.evangeliakostop.paymentsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FraudApiRequest {
    private double amount;
    private String currency;
    private String paymentType;
    private String transactionType;
    private String userId;
    private String bin;
    @JsonProperty("day_of_week")
    private int dayOfWeek;
    private int hour;
}
