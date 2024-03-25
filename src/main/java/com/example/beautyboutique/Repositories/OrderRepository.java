package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.DTOs.Responses.Order.OrdersSummaryDTO;
import com.example.beautyboutique.Models.Orders;
import com.example.beautyboutique.Services.Order.OrderSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    @Query("SELECT o FROM Orders o LEFT JOIN o.shipDetail sd WHERE (:userId IS NULL OR sd.user.id = :userId)")
    Page<Orders> findByConditions(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT " +
            "SUM(CASE WHEN o.orderStatus.id = 4 THEN 1 ELSE 0 END) AS cancelledOrders, " +
            "SUM(CASE WHEN o.orderStatus.id = 5 THEN 1 ELSE 0 END) AS deliveredOrders, " +
            "SUM(CASE WHEN o.orderStatus.id != 4 THEN o.totalPrice ELSE 0 END) AS totalPrice, " +
            "COUNT(o) AS totalOrders " +
            "FROM Orders o")
    OrderSummary getOrdersSummary();

}
