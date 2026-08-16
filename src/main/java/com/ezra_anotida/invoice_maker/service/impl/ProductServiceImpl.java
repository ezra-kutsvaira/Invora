package com.ezra_anotida.invoice_maker.service.impl;

import com.ezra_anotida.invoice_maker.dto.product.*;
import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.entity.Product;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
import com.ezra_anotida.invoice_maker.exception.*;
import com.ezra_anotida.invoice_maker.mapper.ProductMapper;
import com.ezra_anotida.invoice_maker.mapper.exception.DuplicateResourceException;
import com.ezra_anotida.invoice_maker.mapper.exception.InvalidRequestException;
import com.ezra_anotida.invoice_maker.mapper.exception.ResourceNotFoundException;
import com.ezra_anotida.invoice_maker.repository.OrganizationRepository;
import com.ezra_anotida.invoice_maker.repository.ProductRepository;
import com.ezra_anotida.invoice_maker.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final OrganizationRepository organizationRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, OrganizationRepository organizationRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.organizationRepository = organizationRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse createProduct(Long organizationId, CreateProductRequest request) {

        Organization organization = findActiveOrganization(organizationId);

        validateUniqueName(organizationId, request.name(), null);

        validatePrice(request.unitPrice());

        validateStock(request.stockQuantity());

        Product product = productMapper.toEntity(request);

        product.setOrganization(organization);

        product.setActive(true);

        if (product.getStockQuantity() == null)
            product.setStockQuantity(0);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override @Transactional(readOnly = true)
    public ProductResponse getProductById(Long organizationId, Long productId) {

        return productMapper.toResponse(findProduct(organizationId, productId));
    }

    @Override @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(Long organizationId) {

        findActiveOrganization(organizationId);

        return productMapper.toResponseList(productRepository.
                findByOrganizationId(organizationId));
    }

    @Override @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getProductSummaries(Long organizationId) {

        findActiveOrganization(organizationId);

        return productMapper.toSummaryResponseList(productRepository
                .findByOrganizationIdAndActiveTrue(organizationId));
    }

    @Override
    public ProductResponse updateProduct(Long organizationId, Long productId, UpdateProductRequest request) {

        Product product = findProduct(organizationId, productId);

        validateUniqueName(organizationId, request.name(), product);

        if (request.unitPrice() != null)
            validatePrice(request.unitPrice());

        validateStock(request.stockQuantity());

        productMapper.updateToEntityFromRequest(request, product);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long organizationId, Long productId) {

        Product product = findProduct(organizationId, productId);

        product.setActive(false);

        productRepository.save(product);
    }

    @Override @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(Long organizationId, String keyword) {

        findActiveOrganization(organizationId);

        if (keyword == null || keyword.isBlank()) throw new InvalidRequestException("Keyword cannot be empty");

        return productMapper.toResponseList(productRepository
                .findByOrganizationIdAndProductNameContainingIgnoreCase(organizationId, keyword.trim()));
    }

    @Override @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long organizationId, String category) {

        findActiveOrganization(organizationId);

        validateCategory(category);

        return productMapper.toResponseList(productRepository
                .findByOrganizationIdAndCategoryIgnoreCase(organizationId, category.trim()));
    }

    @Override @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProducts(Long organizationId) {

        findActiveOrganization(organizationId);

        return productMapper.toResponseList(productRepository.findByOrganizationIdAndActiveTrue(organizationId));
    }

    @Override @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProductsByCategory(Long organizationId, String category) {

        findActiveOrganization(organizationId);

        validateCategory(category);

        return productMapper.toResponseList(productRepository.findByOrganizationIdAndActiveTrueAndCategoryIgnoreCase(organizationId, category.trim()));
    }

    private Product findProduct(Long organizationId, Long productId) {

        findActiveOrganization(organizationId);

        validateId(productId, "Product");

        return productRepository.findByIdAndOrganizationId(productId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    private Organization findActiveOrganization(Long organizationId) {

        validateId(organizationId, "Organization");

        return organizationRepository.findByIdAndStatus(organizationId, OrganizationStatus.ACTIVE)

                .orElseThrow(() -> new ResourceNotFoundException("Active organization", "id", organizationId));
    }

    private void validateUniqueName(Long organizationId, String name, Product existing) {

        if (name == null || name.isBlank()) {
            return;
        }

        String normalized = name.trim();

        boolean unchanged = existing != null && existing.getProductName() != null && existing.getProductName().equalsIgnoreCase(normalized);

        if (!unchanged && productRepository.existsByOrganizationIdAndProductNameIgnoreCase(organizationId, normalized)) {
            throw new DuplicateResourceException("Product", "name", name);
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidRequestException("Price must be greater than zero");
        }
        }


    private void validateStock(Integer stock) {
        if (stock != null && stock < 0) {
            throw new InvalidRequestException("Stock quantity cannot be negative");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.isBlank()) throw new InvalidRequestException("Category cannot be empty");
    }

    private void validateId(Long id, String resource) {
        if (id == null || id <= 0) throw new InvalidRequestException(resource + " id must be greater than zero");
    }
}
