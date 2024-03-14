package com.example.beautyboutique.Services.Product;

import com.example.beautyboutique.DTOs.Requests.Product.ProductRequest;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.ProductImage;
import com.example.beautyboutique.DTOs.Responses.Cart.PageCart;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product save(Product product);

    List<Product> findAll(Integer pageNumber, Integer pageSize);

    Product findById(Integer id);

    List<Product> findByName(String productName, Integer pageNumber, Integer pageSize);

    Product saveAfterCheck(Product product);

    Product delete(Integer id);

    Product get(Integer id, Integer pageNumber, Integer pageSize);


    Product createProduct(ProductRequest request) throws Exception;

    List<Product> findProductBycCategoryId(int categoryId);

    ProductImage createProductImage(ProductImage image);
    List<Product> findProductByPositionId(int brandId);

    Product updateProduct(Integer id, ProductRequest productUpdate);
}

