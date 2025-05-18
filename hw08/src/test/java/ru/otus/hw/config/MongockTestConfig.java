package ru.otus.hw.config;

import io.mongock.driver.api.driver.ConnectionDriver;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MongockTestConfig {
    @Bean
    public ConnectionDriver connectionDriver() {
        return Mockito.mock(ConnectionDriver.class);
    }
}
