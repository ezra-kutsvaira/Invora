package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.product.*;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(Long organizationId, CreateProductRequest request);

    ProductResponse getProductById(Long organizationId, Long productId);

    List<ProductResponse> getAllProducts(Long organizationId);

    List<ProductSummaryResponse> getProductSummaries(Long organizationId);

    ProductResponse updateProduct(Long organizationId, Long productId, UpdateProductRequest request);

    void deleteProduct(Long organizationId, Long productId);

    List<ProductResponse> searchProducts(Long organizationId, String keyword);

    List<ProductResponse> getProductsByCategory(Long organizationId, String category);

    List<ProductResponse> getAllActiveProducts(Long organizationId);
    
    List<ProductResponse> getAllActiveProductsByCategory(Long organizationId, String category);
}
