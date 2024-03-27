package com.example.beautyboutique.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cartId")
    @JsonIgnore
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @Column(name = "quantity", columnDefinition = "int")
    private Integer quantity;

    @Column(name = "totalPrice")
    private BigDecimal totalPrice;

    public CartItem(Cart cart, Product product, Integer quantity ,BigDecimal totalPrice) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

//    @PostLoad
//    public void calculateTotalPrice() {
//        if (this.product != null && this.quantity != null) {
//            this.totalPrice = this.product.getSalePrice().multiply(new BigDecimal(this.quantity));
//        }
//    }
}
