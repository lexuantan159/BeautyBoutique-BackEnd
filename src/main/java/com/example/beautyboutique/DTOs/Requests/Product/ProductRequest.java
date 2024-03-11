package com.example.beautyboutique.DTOs.Requests.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductRequest {
    private Integer brandId;
    private Integer categoryId;
    private String productName;
    private BigDecimal actualPrice;
    private BigDecimal salePrice;
    private String description;
    private Integer quantity;
    private String[] imageIds;
    private String[] imageUrls;
}
