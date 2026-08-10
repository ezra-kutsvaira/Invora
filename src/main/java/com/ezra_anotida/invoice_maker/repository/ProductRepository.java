package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndOrganizationId(Long productId, Long organizationId);
    Optional<Product> findByIdAndOrganizationIdAndActiveTrue(Long productId, Long organizationId);
    boolean existsByOrganizationIdAndProductNameIgnoreCase(Long organizationId, String productName);
    List<Product> findByOrganizationId(Long organizationId);
    List<Product> findByOrganizationIdAndActiveTrue(Long organizationId);
    List<Product> findByOrganizationIdAndProductNameContainingIgnoreCase(Long organizationId, String productName);
    List<Product> findByOrganizationIdAndCategoryIgnoreCase(Long organizationId, String category);
    List<Product> findByOrganizationIdAndActiveTrueAndCategoryIgnoreCase(Long organizationId, String category);
}
