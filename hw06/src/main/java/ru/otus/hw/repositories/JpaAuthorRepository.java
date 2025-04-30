package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;


@Repository
public class JpaAuthorRepository implements AuthorRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Author> findById(long id) {
        return em.createQuery("select a from Author a where a.id = :id", Author.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Author> findAll() {

        return em.createQuery("select a from Author a", Author.class)
                .getResultList();
    }
}
