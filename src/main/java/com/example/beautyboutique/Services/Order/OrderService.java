package com.example.beautyboutique.Services.Order;

import com.example.beautyboutique.DTOs.Responses.Cart.PageCart;
import com.example.beautyboutique.DTOs.Responses.Order.*;
import com.example.beautyboutique.DTOs.Responses.ResponseDTO;

public interface OrderService {
        CreatedOrder createOrder(Integer userId, Integer shipDetailId, Integer deliveryId, Integer paymentId, Integer voucherId,Integer[] cartItemsId);
        PageOrder getOrderHistory(Integer  userId, Integer pageNo, Integer pageSize, String sortBy, String sortDir);
        PageOrder getAllOrder(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
        OrdersSummaryDTO summaryOrders();

        CancelOrder cancelOrder(Integer userId, Integer orderId);
        UpdateOrder updateShipDetailOrder(Integer userId, Integer orderId, Integer shipDetailId);
        UpdateOrder updateStatusOrder(Integer userId, Integer orderId, Boolean isAccept);

        ResponseDTO changeStatusOrder(Integer userId, Integer orderItemId, Integer statusId);
}
