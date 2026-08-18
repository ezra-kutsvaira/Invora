package com.ezra_anotida.invoice_maker.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LiquibaseMigrationIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldApplyAllLiquibaseMigrations(){
        List<String> appliedChangeSets = jdbcTemplate.queryForList(
                """
                      SELECT id
                      FROM databaseChangeLog
                      ORDER BY orderexecuted
                      """,
                      String.class
        );

        assertThat(appliedChangeSets)
                .contains(
                        "001-create-organizations",
                        "002-create-users",
                        "003-create-organization-memberships",
                        "004-create-company-profiles",
                        "005-create-currencies",
                        "006-create-customers",
                        "007-create-products",
                        "008-create-tax-rates",
                        "009-create-invoices",
                        "010-create-invoice-items",
                        "011-create-payments",
                        "012-create-receipts",
                        "013-create-audit-logs"
                );
    }

    @Test
    void shouldCreateExpectedTables(){
        List<String> tableNames = jdbcTemplate.queryForList(
                """
                        SELECT table name
                        FROM information_schema.tables
                        WHERE table_schema = 'public'
                        """,
                        String.class
        );

        assertThat(tableNames)
                .contains(
                        "organizations",
                        "users",
                        "organization_memberships",
                        "company_profiles",
                        "currencies",
                        "customers",
                        "products",
                        "tax_rates",
                        "invoices",
                        "invoice_items",
                        "payments",
                        "receipts",
                        "audit_logs"
                );

    }

}
