package org.example.mongoktest.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

import java.io.IOException;

@ChangeUnit(id = "init-authors", order = "001", author = "user")
public class InitialDataMigration {

    @Execution
    public void execution() throws IOException {
        // migratsiya logikasi shu yerga yoziladi
    }

    @RollbackExecution
    public void rollback() {
        // optional: rollback qilinadigan ishlar
    }
}
