package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
        Page<Product> findByProductNameContaining(String ProductName, Pageable pageable);
    @Query(value = "SELECT * FROM product WHERE category_id = :categoryId", nativeQuery = true)
    List<Product> findProductByCategoryId(Integer categoryId);
    @Query(value = "SELECT * FROM product WHERE brand_id = :brandId", nativeQuery = true)
    List<Product> findProductByBrandId(Integer brandId);
    boolean existsByProductName(String productName);
}
