package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.config.TestContainersConfiguration;
import com.ezra_anotida.invoice_maker.entity.Organization;
import com.ezra_anotida.invoice_maker.enums.OrganizationStatus;
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
class OrganizationRepositoryIntegrationTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void shouldSaveAndFindOrganizationBySlugIgnoreCase(){

        Organization organization = new Organization();
        organization.setName("Invora Technologies");
        organization.setSlug("invora-technologies");
        organization.setStatus(OrganizationStatus.ACTIVE);

        organizationRepository.saveAndFlush(organization);

        var result = organizationRepository.findBySlugIgnoreCase("INVORA-TECHNOLOGIES");

        assertThat(result).isPresent();
        assertThat(result.get().getName())
                .isEqualTo("Invora Technologies");
        assertThat(result.get().getStatus())
                .isEqualTo(OrganizationStatus.ACTIVE);
    }

    @Test
    void shouldRejectDuplicateOrganizationSlug(){
        Organization first = new Organization();
        first.setName("First Organization");
        first.setSlug("duplicate-slug");

        organizationRepository.saveAndFlush(first);

        Organization second = new Organization();
        second.setName("Second Organization");
        second.setSlug("duplicate-slug");

        assertThatThrownBy(
                () -> organizationRepository.saveAndFlush(second)
        ).isInstanceOf(DataIntegrityViolationException.class);

    }
}
