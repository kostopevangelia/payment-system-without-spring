package com.evangeliakostop.paymentsystem.common.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {

    REQUIRES_PAYMENT_METHOD(0, "requires_payment_method"),
    PENDING(1, "pending"),
    COMPLETED(2, "completed"),
    FAILED(3, "failed"),
    CANCELLED(4, "cancelled");

    private Integer code;
    private String description;

}
