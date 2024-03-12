package com.example.beautyboutique.Controllers;

<<<<<<< HEAD
import com.example.beautyboutique.DTOs.Requests.Blog.BlogRequest;
import com.example.beautyboutique.DTOs.Requests.Product.ProductRequest;
import com.example.beautyboutique.Models.*;
import com.example.beautyboutique.Payload.Response.ResponseMessage;
=======
import com.example.beautyboutique.DTOs.Requests.Product.ProductRequest;
import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Models.Product;
>>>>>>> 007f10876fe2225d402cdf711daebec16fa72bc1
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
<<<<<<< HEAD
import java.util.Optional;
import java.util.stream.IntStream;
=======
>>>>>>> 007f10876fe2225d402cdf711daebec16fa72bc1

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

<<<<<<< HEAD

    @GetMapping("/get-all") //http://localhost:8080/api/product/get-all
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
=======
    @GetMapping("/get-all")
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize) {
        List<Product> products = productService.findAll(pageNumber, pageSize);
>>>>>>> 007f10876fe2225d402cdf711daebec16fa72bc1
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
<<<<<<< HEAD
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
=======
    public @ResponseBody ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        try {
            Product createdProduct = productService.createProduct(request);
            if (createdProduct != null) {
>>>>>>> 007f10876fe2225d402cdf711daebec16fa72bc1
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
