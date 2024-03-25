package com.example.beautyboutique.DTOs.Responses.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrdersSummaryDTO {
    private Integer cancelledOrders;
    private Integer deliveredOrders;
    private BigDecimal totalPrice;
    private Integer totalOrders;
}
