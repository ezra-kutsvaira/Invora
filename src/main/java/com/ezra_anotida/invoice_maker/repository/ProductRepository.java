package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByIdAndOrganizationId(Long productId, Long organizationId);

    boolean existsByOrganizationIdAndProductNameIgnoreCase(Long organizationId, String productName);

    List<Product> findByActiveTrue();

    List<Product> findByProductNameContainingIgnoreCase(String productName);

    boolean existByProductNameIgnoreCase(String productName);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByActiveTrueAndCategoryIgnoreCase(String category);

}
