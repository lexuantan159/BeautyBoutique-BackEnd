package com.example.beautyboutique.Services.Brand;

import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Models.Brand;
import com.example.beautyboutique.Repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl  implements   BrandService{
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Brand save(Brand brand) {
        return brandRepository.save(brand) ;
    }

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand with id " + id + " null"));
    }

    @Override
    public List<Brand> findByName(String brandName) {
        return brandRepository.findBrandByBrandNameContaining(brandName);
    }

    @Override
    public Brand saveafftercheck(Brand brand) {
        if(BrandRepository.existsByBrandName(brand.getBrandName())) {
            System.out.println("Ten san pham da ton tai");
            return  null;
        } else
            return brandRepository.save(brand);
    }

    @Override
    public Brand delete(Integer id) {
        Brand deletebrand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand voi id " + id + " da duoc xoa"));
         brandRepository.deleteById(id);
        System.out.println("Xoa Thanh Cong");
        return deletebrand;
    }

    @Override
    public Brand get(Integer id) {
        return brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand with id " + id + " null"));
    }
}
