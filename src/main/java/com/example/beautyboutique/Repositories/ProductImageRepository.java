package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage,String> {
}
