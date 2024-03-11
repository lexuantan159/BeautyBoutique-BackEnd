package com.example.beautyboutique.Services.Product;
import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Models.BlogImage;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.ProductImage;
import com.example.beautyboutique.Repositories.ProductImageRepository;
import com.example.beautyboutique.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl  implements  ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product saveafftercheck(Product product) {
        if (productRepository.existsByProductName(product.getProductName())) {
            System.out.println("Product name already exists");
            return null;
        }
        return productRepository.save(product);
    }

    public Product delete(Integer id) {
        Product productToDelete = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(productToDelete);
        return productToDelete;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id "+ id +" not found "));
    }

    @Override
    public List<Product> findByName(String productName) {
        List<Product> products = productRepository.findByProductNameContaining(productName);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("User with name " + productName + " not found");
        }
        return products;
    }

    @Override
    public  List<Product> findProductBycCategoryId(int categoryId) {
        return this.productRepository.findProductByCategoryId(categoryId);
    }
    public ProductImage createProductImage(ProductImage image){
        return productImageRepository.save(image);
    }
}
