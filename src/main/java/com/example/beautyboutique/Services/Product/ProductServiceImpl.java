package com.example.beautyboutique.Services.Product;

import com.example.beautyboutique.DTOs.Requests.Product.ProductRequest;
import com.example.beautyboutique.Exception.ResourceNotFoundException;

import com.example.beautyboutique.Models.Brand;
import com.example.beautyboutique.Models.Category;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.ProductImage;
import com.example.beautyboutique.Repositories.BrandRepository;
import com.example.beautyboutique.Repositories.CategoryRepository;
import com.example.beautyboutique.Repositories.ProductImageRepository;
import com.example.beautyboutique.Repositories.ProductRepository;
import com.example.beautyboutique.Services.Brand.BrandService;
import com.example.beautyboutique.Services.Category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product saveAfterCheck(Product product) throws DataIntegrityViolationException {
        if (productRepository.existsByProductName(product.getProductName())) {
            throw new DataIntegrityViolationException("Product name already exists");
        }
        return productRepository.save(product);
    }

    public Product delete(Integer id) throws ResourceNotFoundException {
        Product productToDelete = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(productToDelete);
        return productToDelete;
    }

    @Override
    public Product get(Integer id, Integer pageNumber, Integer pageSize) throws ResourceNotFoundException {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<Product> productPage = productRepository.findAll(pageable);
            if (pageNumber > productPage.getTotalPages()) {
                throw new ResourceNotFoundException("Page number exceeds total pages");
            }

            List<Product> products = productPage.getContent();
            if (id > products.size()) {
                throw new ResourceNotFoundException("Product with id " + id + " not found on the requested page");
            }

            return products.get(id - 1);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
    }


    @Override
    public List<Product> findAll(Integer pageNumber, Integer pageSize) throws RuntimeException {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<Product> productPage = productRepository.findAll(pageable);

            return productPage.getContent();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving products", e);
        }
    }

    @Override
    public Product findById(Integer id) throws ResourceNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found "));
    }

    @Override
    public List<Product> findByName(String productName, Integer pageNumber, Integer pageSize) throws ResourceNotFoundException {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Product> productsPage = productRepository.findByProductNameContaining(productName, pageable);
        if (productsPage.isEmpty()) {
            throw new ResourceNotFoundException("Product with name '" + productName + "' not found");
        }

        return productsPage.getContent();
    }

    public ProductImage createProductImage(ProductImage image) {
        return productImageRepository.save(image);
    }

    @Override
    public List<Product> findProductBycCategoryId(int categoryId) throws ResourceNotFoundException {
        try {
            List<Product> products = this.productRepository.findProductByCategoryId(categoryId);
            if (products.isEmpty()) {
                throw new ResourceNotFoundException("Products with category id " + categoryId + " not found");
            }
            return products;
        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public List<Product> findProductByBrandId(int brandId) throws ResourceNotFoundException {
        try {
            List<Product> products = this.productRepository.findProductByBrandId(brandId);
            if (products.isEmpty()) {
                throw new ResourceNotFoundException("Products with brand id " + brandId + " not found");
            }
            return products;
        } catch (Exception e) {
                throw e;
        }
    }


    public void createProductImages(Product product, String[] imageIds, String[] imageUrls) throws Exception {
        if (imageIds.length != imageUrls.length) {
            throw new Exception("Invalid imageIds or imageUrls");
        }

        for (int i = 0; i < imageIds.length; i++) {
            ProductImage image = new ProductImage();
            image.setId(imageIds[i]);
            image.setImageUrl(imageUrls[i]);
            image.setProduct(product);
            productImageRepository.save(image); // Assuming productImageService has a save method
        }
    }

    public Product updateProduct(Integer id, ProductRequest productUpdate) throws ResourceNotFoundException, DataIntegrityViolationException {
        if (productUpdate == null) {
            throw new IllegalArgumentException("Product update data cannot be null");
        }

        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (!productOptional.isPresent()) {
                throw new ResourceNotFoundException("Product not found");
            }

            Product product = productOptional.get();

            if (productUpdate.getCategoryId() != null) {
                Optional<Category> newCategoryOptional = categoryRepository.findById(productUpdate.getCategoryId());
                if (!newCategoryOptional.isPresent()) {
                    throw new ResourceNotFoundException("Category not found");
                }
                Category newCategory = newCategoryOptional.get();
                product.setCategory(newCategory);
            }

            if (productUpdate.getBrandId() != null) {
                Optional<Brand> newBrandOptional = brandRepository.findById(productUpdate.getBrandId());
                if (!newBrandOptional.isPresent()) {
                    throw new ResourceNotFoundException("Brand not found");
                }
                Brand newBrand = newBrandOptional.get();
                product.setBrand(newBrand);
            }
            product.setProductName(productUpdate.getProductName());
            product.setQuantity(productUpdate.getQuantity());
            product.setDescription(productUpdate.getDescription());
            product.setActualPrice(productUpdate.getActualPrice());
            product.setSalePrice(product.getSalePrice());

            if (productUpdate.getImageIds() != null && productUpdate.getImageUrls() != null) {
                IntStream.range(0, productUpdate.getImageIds().length)
                        .forEach(index -> {
                            String imageId = productUpdate.getImageIds()[index];
                            String imageUrl = productUpdate.getImageUrls()[index];
                            ProductImage image = new ProductImage();
                            image.setId(imageId);
                            image.setImageUrl(imageUrl);
                            image.setProduct(product);
                            productImageRepository.save(image);
                        });
            }

            return product;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Product createProduct(ProductRequest request) throws Exception {
        String[] imageIds = request.getImageIds();
        String[] imageUrls = request.getImageUrls();
        Category categoryData = categoryService.findById(request.getCategoryId());
        Brand brandData = brandService.findById(request.getBrandId());
        Product product = new Product();
        product.setBrand(brandData);
        product.setCategory(categoryData);
        product.setProductName(request.getProductName());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        product.setActualPrice(request.getActualPrice());
        product.setSalePrice(request.getSalePrice());
        Product createdProduct = productRepository.save(product);
        if (createdProduct != null) {
            IntStream.range(0, request.getImageIds().length)
                    .forEach(index -> {
                        String imageId = request.getImageIds()[index];
                        String imageUrl = request.getImageUrls()[index];
                        ProductImage image = new ProductImage();
                        image.setId(imageId);
                        image.setImageUrl(imageUrl);
                        image.setProduct(createdProduct);
                        productImageRepository.save(image);
                    });
        }
        return createdProduct;
    }
}
