package com.example.beautyboutique.DTOs.Responses.Product;

import com.example.beautyboutique.Models.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductReponse {
    List<Product> productList;
}
