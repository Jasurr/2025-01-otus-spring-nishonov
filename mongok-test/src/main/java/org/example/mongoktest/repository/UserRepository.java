package org.example.mongoktest.repository;

import org.example.mongoktest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
