package com.example.beautyboutique.DTOs.Requests.Product;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
=======
import lombok.*;
>>>>>>> 007f10876fe2225d402cdf711daebec16fa72bc1

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
@Getter
@Setter
=======
@Data
>>>>>>> 007f10876fe2225d402cdf711daebec16fa72bc1
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