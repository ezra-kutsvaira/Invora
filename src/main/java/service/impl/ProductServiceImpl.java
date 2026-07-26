package service.impl;

import com.ezra_anotida.invoice_maker.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.exception.ResourceNotFoundException;
import dto.product.CreateProductRequest;
import dto.product.ProductResponse;
import dto.product.ProductSummaryResponse;
import dto.product.UpdateProductRequest;
import entity.Product;
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
            throw new InvalidRequestException("Keyword cannot be empty");

        }

        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword.trim());
        return productMapper.toResponseList(products);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {

        //Work on Null Pointers
        if(category == null || category.isBlank()){
            throw new InvalidRequestException("Category cannot be empty");
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
            throw new InvalidRequestException("Category cannot be empty");
        }
        List<Product> products = productRepository.findByNameContainingIgnoreCase(category.trim());

        return productMapper.toResponseList(products);
    }

    //Helper Methods
    private Product findProductById(Long productId) {
        if(productId == null){
            throw new InvalidRequestException("Product ID cannot be null");
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    private void validateUniqueName (String name, Product existingProduct) {
        if(name == null || name.isBlank()){
            return;
        }

        boolean nameBelongsToCurrentProduct = existingProduct != null && existingProduct.getProductName() != null && existingProduct.getProductName().equals(name);

        if(!nameBelongsToCurrentProduct && productRepository.existByNameIgnoreCase(name)){
            throw new DuplicateResourceException("Product", "name", name);
        }

    }

    private void validatePrice(BigDecimal price){
        if(price == null){
            throw new InvalidRequestException("Price cannot be null");
        }

        if(price.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidRequestException("Price must be greater than zero");
        }
    }

    private void validateStockQuantity(Integer stockQuantity){
        if (stockQuantity == null) {
            return;
        }

        if(stockQuantity < 0){
            throw new InvalidRequestException("Stock quantity cannot be negative");
        }
    }


}
