package com.codefactory.reservasmscatalogservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Application context test for MS-Catalog-Service.
 * Verifies that Spring context loads successfully with test configuration.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ReservasMsCatalogServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that Spring Boot application context loads successfully
    }
}