package com.evangeliakostop.paymentsystem.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentInfo {
    @JsonProperty("client_secret")
    private String client_secret;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("currency")
    private String currency;
    private LocalDateTime timestamp;
    private String paymentType;
    @JsonProperty("status")
    private String status;
    private boolean isFraud;
    private double fraudScore;
    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentInfo that = (PaymentInfo) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency); // Exclude transactionId
    }
}
