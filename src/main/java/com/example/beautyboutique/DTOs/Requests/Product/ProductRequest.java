package com.example.beautyboutique.DTOs.Requests.Product;


import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor

@Data
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
