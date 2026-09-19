package com.evangeliakostop.paymentsystem.models;

import com.evangeliakostop.paymentsystem.common.utils.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PaymentStatusResponse {
    private String transactionId;
    private PaymentStatus status;
}
