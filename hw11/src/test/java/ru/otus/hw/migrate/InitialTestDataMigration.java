package ru.otus.hw.migrate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import ru.otus.hw.migration.InitialDataMigration;

@Component
@Import(InitialDataMigration.class)
public class InitialTestDataMigration {
    @Autowired
    private InitialDataMigration initialDataMigration;

    @BeforeEach
    void setUp() {
        initialDataMigration.clearAndMigrate()
                .block();
    }
}
