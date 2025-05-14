package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {
    private final EntityManager em;

    @Override
    public List<Comment> findByBookId(long bookId) {
        Book book = em.find(Book.class, bookId);
        return em.createQuery(
                        "select c from Comment c where c.book = :book", Comment.class)
                .setParameter("book", book)
                .getResultList();
    }

    @Override
    public Optional<Comment> findById(long id) {
        return em.createQuery("select c from Comment c where c.id = :id", Comment.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void deleteById(long id) {
        var comment = em.find(Comment.class, id);
        if (comment != null) {
            em.remove(comment);
        }
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }
}
