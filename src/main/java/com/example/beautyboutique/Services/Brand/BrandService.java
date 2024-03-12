package com.example.beautyboutique.Services.Brand;

import com.example.beautyboutique.Models.Brand;
import com.example.beautyboutique.Models.Category;

import java.util.List;

public interface BrandService {

    Brand save(Brand brand);

    List<Brand> findAll();

    Brand findById(Integer id);


    List<Brand> findByName(String brandName);

    Brand saveafftercheck(Brand brand);

    Brand delete(Integer id);

    Brand get(Integer id);
}
