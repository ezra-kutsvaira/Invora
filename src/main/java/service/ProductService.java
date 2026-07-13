package service;

import dto.product.CreateProductRequest;
import dto.product.ProductResponse;
import dto.product.ProductSummaryResponse;
import dto.product.UpdateProductRequest;
import entity.Product;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProductById(Long productId);

    List<ProductResponse> getAllProducts();

    List<ProductSummaryResponse> getProductSummaries();

    ProductResponse updateProduct(Long productId, UpdateProductRequest request);

    void deleteProduct(Long productId);

    List<ProductResponse> searchProducts(String keyword);

    List<ProductResponse> getProductsByCategory(String category);

    List<ProductResponse> getAllActiveProducts();

    List<ProductResponse> getAllActiveProductsByCategory(String category);

}
