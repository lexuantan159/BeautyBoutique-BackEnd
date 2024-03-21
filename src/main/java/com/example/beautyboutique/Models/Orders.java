package com.example.beautyboutique.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "shipDetailId")
    private ShipDetail shipDetail;

    @Column(name = "createdAt", columnDefinition = "DATETIME", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "totalPrice", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "deliveryId")
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "voucherId")
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "orderStatusId")
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "orders", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderDetail> ordersDetails;

    public Orders(ShipDetail shipDetail, BigDecimal totalPrice, Delivery delivery, Payment payment, OrderStatus orderStatus, Voucher voucher) {
        this.shipDetail  = shipDetail;
        this.totalPrice = totalPrice;
        this.delivery = delivery;
        this.payment = payment;
        this.orderStatus = orderStatus;
        this.voucher = voucher;
    }
    public Orders(ShipDetail shipDetail, BigDecimal totalPrice, Delivery delivery, Payment payment, OrderStatus orderStatus) {
        this.shipDetail  = shipDetail;
        this.totalPrice = totalPrice;
        this.delivery = delivery;
        this.payment = payment;
        this.orderStatus = orderStatus;
    }
}
