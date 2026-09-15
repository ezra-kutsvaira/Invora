package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.OrganizationMembership;
import com.ezra_anotida.invoice_maker.enums.MembershipStatus;
import com.ezra_anotida.invoice_maker.enums.OrganizationRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, Long> {

    Optional<OrganizationMembership> findByIdAndOrganizationId(Long id, Long organizationId);

    Optional<OrganizationMembership> findByOrganizationIdAndUserId(Long organizationId, Long userId);

    @EntityGraph(attributePaths = {"organization", "user"})
    Optional<OrganizationMembership> findByOrganizationIdAndUserIdAndStatus(Long organizationId, Long userId, MembershipStatus membershipStatus);

    boolean existsByOrganizationIdAndUserId(Long organizationId, Long userId);

    boolean existsByOrganizationIdAndUserIdAndStatus(Long organizationId, Long userId, MembershipStatus membershipStatus);

    Page<OrganizationMembership> findByOrganizationId(Long organizationId, Pageable pageable);

    Page<OrganizationMembership> findByOrganizationIdAndStatus(Long organizationId, MembershipStatus status, Pageable pageable);

    long countByOrganizationIdAndRoleAndStatus(Long organizationId, OrganizationRole organizationRole , MembershipStatus membershipStatus);
}
