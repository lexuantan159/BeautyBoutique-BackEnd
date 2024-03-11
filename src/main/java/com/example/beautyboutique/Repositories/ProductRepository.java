package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
        List<Product> findByProductNameContaining(String partialProductName);
    @Query(value = "SELECT * FROM product WHERE category_id = :categoryId", nativeQuery = true)
    List<Product> findProductByCategoryId(Integer categoryId);
    boolean existsByProductName(String productName);
}
