package com.ezra_anotida.invoice_maker.service;

import com.ezra_anotida.invoice_maker.dto.product.CreateProductRequest;
import com.ezra_anotida.invoice_maker.dto.product.ProductResponse;
import com.ezra_anotida.invoice_maker.dto.product.ProductSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.product.UpdateProductRequest;

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
