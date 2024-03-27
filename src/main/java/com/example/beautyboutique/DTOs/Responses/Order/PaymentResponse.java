package com.example.beautyboutique.DTOs.Responses.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentResponse {
    private String message;
    private String paymentUrl;
    private String appTransId;
    private String zpTransToken;
}
