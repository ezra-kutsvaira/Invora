package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findBySlugIgnoreCase(String slug);

    Optional<Organization> findByIdAndActiveTrue(Long organizationId);

    boolean existsBySlugIgnoreCase(String slug);

    Page<Organization> findByActiveTrue(Pageable pageable);

    Page<Organization> findByActiveFalse(Pageable pageable);

    Page<Organization> findByStatus(OrganizationStatus status, Pageable pageable);

    Page<Organization> findByNameContainingIgnoreCase(String name, Pageable pageable);
}