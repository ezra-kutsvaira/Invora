package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.OrganizationMembership;
import com.ezra_anotida.invoice_maker.enums.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, Long> {

    Optional<OrganizationMembership> findByIdAndOrganizationId(Long id, Long organizationId);

    Optional<OrganizationMembership> findByOrganizationIdAndUserId(Long organizationId, Long userId);

    boolean existsByOrganizationIdAndUserId(Long organizationId, Long userId);

    Page<OrganizationMembership> findByOrganizationId(Long organizationId, Pageable pageable);

    Page<OrganizationMembership> findByOrganizationIdAndStatus(
            Long organizationId,
            MembershipStatus status,
            Pageable pageable
    );
}
