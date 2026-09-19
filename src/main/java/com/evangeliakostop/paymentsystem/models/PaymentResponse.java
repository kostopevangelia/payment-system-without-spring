package com.evangeliakostop.paymentsystem.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
public class PaymentResponse extends CommonResponse {
    private PaymentInfo paymentInfo;
}
