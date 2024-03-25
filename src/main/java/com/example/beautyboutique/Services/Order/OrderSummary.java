package com.example.beautyboutique.Services.Order;

import java.math.BigDecimal;

public interface OrderSummary {
    Integer getCancelledOrders();
    Integer getDeliveredOrders();
    BigDecimal getTotalPrice();
    Integer getTotalOrders();
}
