package com.ezra_anotida.invoice_maker.controller;

import com.ezra_anotida.invoice_maker.dto.product.CreateProductRequest;
import com.ezra_anotida.invoice_maker.dto.product.ProductResponse;
import com.ezra_anotida.invoice_maker.dto.product.ProductSummaryResponse;
import com.ezra_anotida.invoice_maker.dto.product.UpdateProductRequest;
import com.ezra_anotida.invoice_maker.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/{organizationId}/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@PathVariable("organizationId") Long organizationId, @Valid @RequestBody CreateProductRequest request) {

        ProductResponse product = productService.createProduct(organizationId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("organizationId") Long organizationId, @PathVariable("productId") Long productId) {

        ProductResponse product = productService.getProductById(organizationId, productId);

        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(@PathVariable("organizationId") Long organizationId) {

        List<ProductResponse> products = productService.getAllProducts(organizationId);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<ProductSummaryResponse>> getProductSummaries(@PathVariable("organizationId") Long organizationId) {

        List<ProductSummaryResponse> summaries = productService.getProductSummaries(organizationId);

        return ResponseEntity.ok(summaries);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("organizationId") Long organizationId, @PathVariable("productId") Long productId, @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse updatedProduct = productService.updateProduct(organizationId, productId, request);

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("organizationId") Long organizationId, @PathVariable("productId") Long productId) {

        productService.deleteProduct(organizationId, productId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@PathVariable("organizationId") Long organizationId, @RequestParam("keyword") String keyword) {

        List<ProductResponse> products = productService.searchProducts(organizationId, keyword);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/category")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable("organizationId") Long organizationId, @RequestParam("category") String category) {

        List<ProductResponse> products = productService.getProductsByCategory(organizationId, category);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getAllActiveProducts(@PathVariable("organizationId") Long organizationId) {

        List<ProductResponse> products = productService.getAllActiveProducts(organizationId);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/active/category")
    public ResponseEntity<List<ProductResponse>> getAllActiveProductsByCategory(@PathVariable("organizationId") Long organizationId, @RequestParam("category") String category) {

        List<ProductResponse> products = productService.getAllActiveProductsByCategory(organizationId, category);

        return ResponseEntity.ok(products);
    }
}