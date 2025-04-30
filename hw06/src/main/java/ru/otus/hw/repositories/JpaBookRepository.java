package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class JpaBookRepository implements BookRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Book> findAllWithGenres() {
        EntityGraph<?> entityGraph = em.createEntityGraph("Book.withGenresAndAuthor");
        TypedQuery<Book> query = em.createQuery("select b from Book b", Book.class);
        query.setHint("javax.persistence.fetchgraph", entityGraph);
        return query.getResultList();
    }

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraph = em.createEntityGraph("Book.withGenresAndAuthor");
        TypedQuery<Book> query = em.createQuery("select b from Book b where b.id = :id", Book.class);
        query.setHint("javax.persistence.fetchgraph", entityGraph);

        return query
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void deleteById(long id) {
        em.createQuery("delete from Book b where b.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }
}
