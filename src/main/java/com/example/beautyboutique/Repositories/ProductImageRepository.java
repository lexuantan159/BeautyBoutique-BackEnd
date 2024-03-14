package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    @Query(value = "SELECT * FROM product WHERE product_id = :productId", nativeQuery = true)
    List<ProductImage> findProductImageByProductId(Integer productId);
}

