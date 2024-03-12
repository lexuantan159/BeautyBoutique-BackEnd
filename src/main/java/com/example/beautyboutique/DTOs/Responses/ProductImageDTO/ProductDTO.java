package com.example.beautyboutique.DTOs.Responses.ProductImageDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDTO {
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
