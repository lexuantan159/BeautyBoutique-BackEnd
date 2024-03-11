package com.example.beautyboutique.Services.Category;

import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Models.Category;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements  CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Category> findByName(String categoryName) {
        return this.categoryRepository.findByCategoryNameContaining(categoryName);
    }


    public Category saveafftercheck(Category category) {
        if (CategoryRepository.existsByCategoryName(category.getCategoryName())) {
            System.out.println("Category name already exists");
            return null;
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category delete(Integer id) {
        Category deletedCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        // Xóa sản phẩm
        categoryRepository.deleteById(id);


        return deletedCategory;
    }

    @Override
    public Category get(Integer id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " null"));
    }
}
