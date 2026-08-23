package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.config.TestContainersConfiguration;
import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.entity.OrganizationMembership;
import com.ezra_anotida.invoice_maker.entity.User;
import com.ezra_anotida.invoice_maker.enums.MembershipStatus;
import com.ezra_anotida.invoice_maker.enums.OrganizationRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
@Transactional
class OrganizationMembershipRepositoryIntegrationTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationMembershipRepository organizationMembershipRepository;

    @Test
    void shouldOnlyFindMembershipUnderCorrectOrganization(){

        Organization firstOrganization = createOrganization("First Business" , "first-business");

        Organization secondOrganization = createOrganization("Second Business" , "second-business");

        User user = createUser("Ezra", "ezra@invora.co.zw");

        OrganizationMembership organizationMembership = new OrganizationMembership();
        organizationMembership.setOrganization(firstOrganization);
        organizationMembership.setUser(user);
        organizationMembership.setStatus(MembershipStatus.ACTIVE);

        OrganizationMembership saved = organizationMembershipRepository.saveAndFlush(organizationMembership);

        assertThat(organizationMembershipRepository.findByIdAndOrganizationId(
                saved.getId(),firstOrganization.getId()))
                .isPresent();

        assertThat(organizationMembershipRepository.findByIdAndOrganizationId(
                saved.getId(),secondOrganization.getId()))
                .isEmpty();
    }

    @Test
    void shouldRejectDuplicateMembershipForSameOrganizationAndUser(){

        Organization organization = createOrganization("Invora", "invora");

        User user = createUser("Ezra", "ezra@example.com");

        OrganizationMembership first =  createMembership(organization, user);

        organizationMembershipRepository.saveAndFlush(first);

        OrganizationMembership duplicate = createMembership(organization, user);

        assertThatThrownBy(() -> organizationMembershipRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    private Organization createOrganization(String name, String slug) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setSlug(slug);

        return organizationRepository.saveAndFlush(organization);
    }

    private User createUser (String name, String email){

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("encoded-test-password");
        user.setEnabled(true);

        return userRepository.saveAndFlush(user);
    }

    private OrganizationMembership createMembership(Organization organization, User user) {

        OrganizationMembership organizationMembership = new OrganizationMembership();

        organizationMembership.setOrganization(organization);
        organizationMembership.setUser(user);
        organizationMembership.setRole(OrganizationRole.OWNER);
        organizationMembership.setStatus(MembershipStatus.ACTIVE);

        return organizationMembership;
    }
}
