package com.example.beautyboutique.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int", nullable = false)
    private Integer id;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "discount", columnDefinition = "double")
    private Float discount;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "quantity", columnDefinition = "int")
    private Integer quantity;

    @Column(name = "numUsedVoucher", columnDefinition = "int")
    private Integer numUsedVoucher;

    @Column(name = "minimumOrder", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal minimumOrder;

    @Column(name = "maximDiscount", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal maximDiscount;

    @Column(name = "startDate", columnDefinition = "DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "endDate", columnDefinition = "DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

}
