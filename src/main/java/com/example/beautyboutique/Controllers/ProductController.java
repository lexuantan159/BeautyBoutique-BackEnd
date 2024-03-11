package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Requests.Blog.BlogRequest;
import com.example.beautyboutique.DTOs.Requests.Product.ProductRequest;
import com.example.beautyboutique.Models.*;
import com.example.beautyboutique.Payload.Response.ResponseMessage;
import com.example.beautyboutique.Services.Brand.BrandService;
import com.example.beautyboutique.Services.Brand.BrandServiceImpl;
import com.example.beautyboutique.Services.Category.CategoryService;
import com.example.beautyboutique.Services.Category.CategoryServiceImpl;
import com.example.beautyboutique.Services.Product.ProductService;
import com.example.beautyboutique.Services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    public @ResponseBody ResponseEntity<?> createProduct(ProductRequest request) {
        try {
            String[] imageIds = request.getImageIds();
            String[] imageUrls = request.getImageUrls();
            System.out.printf("image " + request.getImageIds());
            if (imageIds == null || imageUrls == null || imageIds.length != imageUrls.length) {
                return new ResponseEntity<>("Invalid imageIds or imageUrls", HttpStatus.BAD_REQUEST);
            }
            Integer brandId = request.getBrandId();
            Brand brandData = brandService.findById(brandId);
            Integer categoryId = request.getCategoryId();
            Category categoryData = categoryService.findById(categoryId);

            Product product = new Product();
            product.setBrand(brandData);
            product.setCategory(categoryData);
            product.setProductName(request.getProductName());
            product.setQuantity(request.getQuantity());
            product.setDescription(request.getDescription());
            product.setActualPrice(request.getActualPrice());
            product.setSalePrice(request.getSalePrice());

            Product createdProduct = productService.save(product);
            if (createdProduct != null) {
                IntStream.range(0, imageIds.length).forEach(index -> {
                    String imageId = imageIds[index];
                    String imageUrl = imageUrls[index];
                    ProductImage image = new ProductImage();
                    image.setId(imageId);
                    image.setImageUrl(imageUrl);
                    image.setProduct(createdProduct);
                    productService.createProductImage(image);
                });
                return new ResponseEntity<>("Created a successful Product", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to create Product", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Failed to create Product", HttpStatus.BAD_REQUEST);
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
