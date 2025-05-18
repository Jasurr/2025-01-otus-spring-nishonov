package org.example.mongoktest.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.example.mongoktest.repository.UserRepository;

@ChangeLog(order = "001")
public class InitialDataMigration {

    @ChangeSet(order = "001", id = "initUsers", author = "chatgpt")
    public void initUsers(UserRepository userRepository) {

    }
}
