package com.example.beautyboutique.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int", nullable = false)
    private Integer id;

    @Column(name = "productName", columnDefinition = "nvarchar(500)", nullable = false)
    private String productName;

    @Column(name = "actualPrice")
    private BigDecimal actualPrice;

    @Column(name = "salePrice")
    private BigDecimal salePrice;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "quantity", columnDefinition = "int")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "categoryId", columnDefinition = "int")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brandId", columnDefinition = "int")
    private Brand brand;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<ProductImage> images;

}
