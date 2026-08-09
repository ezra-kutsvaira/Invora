package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByActiveTrue();

    List<Product> findByProductNameContainingIgnoreCase(String productName);

    boolean existByProductNameIgnoreCase(String productName);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByActiveTrueAndCategoryIgnoreCase(String category);

}
