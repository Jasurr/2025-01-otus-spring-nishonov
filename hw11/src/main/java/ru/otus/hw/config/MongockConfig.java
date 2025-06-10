package ru.otus.hw.config;

import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongockConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongockConfig.class);
    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Bean
    public ConnectionDriver connectionDriver(MongoClient mongoClient) {
        LOGGER.info("Configuring Mongock with database: {}", databaseName);
        return MongoReactiveDriver.withDefaultLock(mongoClient, databaseName);
    }
}