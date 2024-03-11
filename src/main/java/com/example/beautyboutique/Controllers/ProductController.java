package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.Models.Brand;
import com.example.beautyboutique.Models.Category;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Payload.Response.ResponseMessage;
import com.example.beautyboutique.Services.Brand.BrandService;
import com.example.beautyboutique.Services.Category.CategoryService;
import com.example.beautyboutique.Services.Product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @GetMapping("/get-all") //http://localhost:8080/api/product/get-all
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }
    @GetMapping("/findById/{id}")//http://localhost:8080/api/product/findById
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        System.out.println("Find by id");
        Product products = productService.findById(id);

        if (products != null) {
            System.out.println(products.getId());
            return new ResponseEntity<>(products, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping(value = "/create-product", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    }, produces =
            MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseMessage> createProduct(@RequestBody Product product) {
        System.out.println("Create new product");
        Product productCreat = productService.saveafftercheck(product);
        if(productCreat == null) {
            return new ResponseEntity<>(new ResponseMessage("Add product fail."), HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(new ResponseMessage("Add product success !!!"), HttpStatus.OK);
        }
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchProduct(@RequestParam String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Product name is required"), HttpStatus.BAD_REQUEST);
        }
        List<Product> products = productService.findByName(productName);
        if (products.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("Product not found"), HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(products);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Product> delete(@PathVariable Integer id) {
        Product deletedProduct = productService.delete(id);
        return new ResponseEntity<>(deletedProduct, HttpStatus.NO_CONTENT);
    }
    @GetMapping("/get/{id}")
    public  ResponseEntity<Product> get(@PathVariable Integer id) {
        Product getProduct = productService.findById(id);
        return  new ResponseEntity<>(getProduct,HttpStatus.OK );
    }
    @GetMapping("/getPbyC/{categoryId}")
    public  ResponseEntity<?> get(@PathVariable int categoryId) {
        List<Product> getPbyC = productService.findProductBycCategoryId(categoryId);
        return  new ResponseEntity<>(getPbyC,HttpStatus.OK);
    }
    @PutMapping("/update/{id}/{categoryId}/{brandId}")
    public  ResponseEntity<Product> updateProduct (@PathVariable Integer id,
                                                   @PathVariable int categoryId,
                                                   @PathVariable int brandId,
                                                   @RequestBody Product productUpdate) {
        Product product  = productService.findById(id);
        if(product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Category category =this.categoryService.get(categoryId);
        Brand brand =this.brandService.get(brandId);
        productUpdate.setCategory(category);
        productUpdate.setBrand(brand);
        productUpdate.setId(id);
        productService.save(productUpdate);
        return new ResponseEntity<>(productUpdate,HttpStatus.OK);
    }

}
