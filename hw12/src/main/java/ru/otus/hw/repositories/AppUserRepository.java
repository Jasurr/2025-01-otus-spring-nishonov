package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    @EntityGraph(value = "AppUser.withUserRoles", type = EntityGraph.EntityGraphType.FETCH)
    Optional<AppUser> findByUsername(String username);
}
