package com.evangeliakostop.paymentsystem.config;

import com.evangeliakostop.paymentsystem.models.PaymentInfo;
import com.evangeliakostop.paymentsystem.models.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

public class PaymentHttpStatusResolver {
    public HttpStatus resolve(PaymentResponse r) {
        if (r == null || r.getPaymentInfo() == null) return HttpStatus.INTERNAL_SERVER_ERROR;

        PaymentInfo info = r.getPaymentInfo();
        if (Boolean.TRUE.equals(info.isFraud())) return HttpStatus.FORBIDDEN;

        String s = info.getStatus() == null ? "" : info.getStatus().toLowerCase();
        return switch (s) {
            case "succeeded" -> HttpStatus.OK;
            case "processing", "requires_capture" -> HttpStatus.ACCEPTED;
            case "requires_action", "requires_confirmation", "requires_payment_method" -> HttpStatus.BAD_REQUEST;
            case "canceled" -> HttpStatus.GONE;
            default -> HttpStatus.OK;
        };
    }
}
