package com.evangeliakostop.paymentsystem.common.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentType {

    CARD("card"),
    IBAN("iban");

    private final String description;

    @Override
    public String toString() {
        return this.description;
    }
}
