package com.evangeliakostop.paymentsystem.models;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PaymentRequest {
    private String cardNumber;
    private String iban;
    private String transactionId;
    private Long amount;
    private String currency;
    private LocalDateTime timestamp;
    private TransactionType transactionType;
    private String paymentType;
    private String userId;
}
