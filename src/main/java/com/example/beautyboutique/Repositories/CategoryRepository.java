package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByCategoryNameContaining(String partialCategoryName);

    public static boolean existsByCategoryName(String categoryName) {
        return false;
    }
}

