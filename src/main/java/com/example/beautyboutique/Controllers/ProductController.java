package com.example.beautyboutique.Controllers;
import com.example.beautyboutique.DTOs.Requests.Product.ProductRequest;
import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Models.Product;
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
@RestController
@RequestMapping("/api/product") //http://localhost:8080/api/product
@CrossOrigin(origins = "http://localhost:3000")
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
                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize) {
        List<Product> products = productService.findAll(pageNumber, pageSize);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Product> findById(@PathVariable(value = "id") Integer id)
                                             {
        System.out.println("Find by id");
        Product products = productService.findById(id);
        if (products != null) {
            return new ResponseEntity<>(products, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping(value = "/create-product", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })

    public @ResponseBody ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        try {
            Product createdProduct = productService.createProduct(request);
            if (createdProduct != null) {
                return new ResponseEntity<>("Created a successful Product", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to create Product", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Failed to create Product", HttpStatus.BAD_REQUEST);
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

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductRequest productUpdate) throws ResourceNotFoundException {
        Product updatedProduct = productService.updateProduct(id, productUpdate);
        return ResponseEntity.ok(updatedProduct);
    }
}
