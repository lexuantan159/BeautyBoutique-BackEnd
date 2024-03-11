package com.example.beautyboutique.Services.Product;

import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.ProductImage;

import java.util.List;

public interface ProductService {

 Product save(Product product);

 List<Product> findAll();

 Product findById(Integer id);

  List<Product> findByName(String productName);

  Product saveafftercheck(Product product);

   Product delete(Integer id);

//   Product get(Integer id);

   List<Product> findProductBycCategoryId(int categoryId);
}

