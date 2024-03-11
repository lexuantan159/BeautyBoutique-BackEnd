package com.example.beautyboutique.Services.Category;

import com.example.beautyboutique.Models.Category;
import com.example.beautyboutique.Models.Product;

import java.util.List;

public interface CategoryService {
    Category save(Category category);

    List<Category> findAll();

    Category findById(Integer id);

    List<Category> findByName(String categoryName);

    Category saveafftercheck(Category category);

    Category delete(Integer id);

    Category get(Integer id);
}
