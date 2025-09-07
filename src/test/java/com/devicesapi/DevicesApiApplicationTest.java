package com.devicesapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DevicesApiApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // If the context fails to load, this test will fail
    }

    @Test
    void applicationStarts() {
        // This test verifies that the application can start without any configuration issues
        // The @SpringBootTest annotation ensures the full application context is loaded
    }
}