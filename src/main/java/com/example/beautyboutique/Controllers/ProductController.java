package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.Models.Brand;
import com.example.beautyboutique.Models.Category;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Payload.Response.ResponseMessage;
import com.example.beautyboutique.Services.Brand.BrandService;
import com.example.beautyboutique.Services.Category.CategoryService;
import com.example.beautyboutique.Services.Product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RequestMapping("/api/product") //http://localhost:8080/api/product
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        List<Product> products = productService.findAll(pageNumber, pageSize);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Product> findById(@PathVariable(value = "id") Integer id,
                                            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        System.out.println("Find by id");
        Product products = productService.get(id, pageNumber, pageSize);
        if (products != null) {
            System.out.println(products.getId());
            return new ResponseEntity<>(products, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createProduct(@RequestBody Product product) {
        System.out.println("Create new product");
        Product productCreat = productService.saveAfterCheck(product);
        if (productCreat == null) {
            return new ResponseEntity<>(new ResponseMessage("Add product fail."), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new ResponseMessage("Add product success !!!"), HttpStatus.OK);
        }
    }

    @GetMapping("/findByName")
    public ResponseEntity<List<Product>> findByName(
            @RequestParam(name = "productName") String productName,
            @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        List<Product> products = productService.findByName(productName, pageNumber, pageSize);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Product> delete(@PathVariable Integer id) {
        Product deletedProduct = productService.delete(id);
        return new ResponseEntity<>(deletedProduct, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Product> get(@PathVariable Integer id) {
        Product getProduct = productService.findById(id);
        return new ResponseEntity<>(getProduct, HttpStatus.OK);
    }

    @GetMapping("/getProductByC/{categoryId}")
    public ResponseEntity<?> get(@PathVariable int categoryId) {
        List<Product> getPbyC = productService.findProductBycCategoryId(categoryId);
        return new ResponseEntity<>(getPbyC, HttpStatus.OK);
    }

    @PutMapping("/update/{id}/{categoryId}/{brandId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id,
                                                 @PathVariable int categoryId,
                                                 @PathVariable int brandId,
                                                 @RequestBody Product productUpdate) {
        Product product = productService.findById(id);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Category category = this.categoryService.get(categoryId);
        Brand brand = this.brandService.get(brandId);
        productUpdate.setCategory(category);
        productUpdate.setBrand(brand);
        productUpdate.setId(id);
        productService.save(productUpdate);
        return new ResponseEntity<>(productUpdate, HttpStatus.OK);
    }
}
