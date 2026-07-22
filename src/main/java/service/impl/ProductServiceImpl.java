package service.impl;

import dto.product.CreateProductRequest;
import dto.product.ProductResponse;
import dto.product.ProductSummaryResponse;
import dto.product.UpdateProductRequest;
import entity.Product;
import jakarta.persistence.EntityNotFoundException;
import mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.ProductRepository;
import service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        validateUniqueName(request.name(), null);
        validatePrice(request.unitPrice());
        validateStockQuantity(request.stockQuantity());


        Product product = productMapper.toEntity(request);

        //Active status
        if(product.getActive() == null){
            product.setActive(true);
        }

        if(product.getStockQuantity() == null){
            product.setStockQuantity(0);
        }

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long productId) {

        Product product = findProductById(productId);

        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();

        return productMapper.toResponseList(products);
    }

    @Override
    public List<ProductSummaryResponse> getProductSummaries() {

        List<Product> products = productRepository.findByActiveTrue();

        return productMapper.toSummaryResponseList(products);
    }

    @Override
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {

        //find the product
        Product existingProduct = findProductById(productId);

        validateUniqueName(request.name(), existingProduct);

        productMapper.updateToEntityFromRequest(request, existingProduct);

        Product updatedProduct = productRepository.save(existingProduct);

        return  productMapper.toResponse(updatedProduct) ;
    }


    @Override
    public void deleteProduct(Long productId) {

        Product product =  findProductById(productId);

        productRepository.delete(product);

    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {

        if(keyword == null || keyword.isBlank()){
            throw new IllegalArgumentException("keyword cannot be empty");

        }

        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword.trim());
        return productMapper.toResponseList(products);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {

        //Work on Null Pointers
        if(category == null || category.isBlank()){
            throw new IllegalArgumentException("category cannot be empty");
        }
        List<Product> products = productRepository.findByNameContainingIgnoreCase(category.trim());

        return productMapper.toResponseList(products);
    }

    @Override
    public List<ProductResponse> getAllActiveProducts() {

        List<Product> products = productRepository.findByActiveTrue();

        return productMapper.toResponseList(products);
    }

    @Override
    public List<ProductResponse> getAllActiveProductsByCategory(String category) {
        if(category == null || category.isBlank()){
            throw new IllegalArgumentException("category cannot be empty");
        }
        List<Product> products = productRepository.findByNameContainingIgnoreCase(category.trim());

        return productMapper.toResponseList(products);
    }

    //Helper Methods
    private Product findProductById(Long productId) {
        if(productId == null){
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        return productRepository.findById(productId)
                .orElseThrow(()-> new IllegalArgumentException("Product with Id" + productId + " cannot be found"));
    }

    private void validateUniqueName (String name, Product existingProduct) {
        if(name == null || name.isBlank()){
            return;
        }

        boolean nameBelongsToCurrentProduct = existingProduct != null && existingProduct.getProductName() != null && existingProduct.getProductName().equals(name);

        if(!nameBelongsToCurrentProduct && productRepository.existByNameIgnoreCase(name)){
            throw new IllegalArgumentException("A product with name " + name + "already exists");
        }

    }

    private void validatePrice(BigDecimal price){
        if(price == null){
            throw  new IllegalArgumentException("Price cannot be null");
        }

        if(price.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    private void validateStockQuantity(Integer stockQuantity){
        if(stockQuantity < 0){
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }


}
