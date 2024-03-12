package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository  extends JpaRepository<Brand, Integer> {
    public static boolean existsByBrandName(String brandName) {
        return false;
    }

    List<Brand> findBrandByBrandNameContaining(String brandName);
}
