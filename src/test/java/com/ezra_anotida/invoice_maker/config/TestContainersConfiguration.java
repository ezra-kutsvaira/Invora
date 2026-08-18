package com.ezra_anotida.invoice_maker.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgreSQLContainer (){
        return new PostgreSQLContainer("postgres:17.6-alpine")
                .withDatabaseName("invora_test")
                .withUsername("invora_test")
                .withPassword("invora_test");
    }
}
