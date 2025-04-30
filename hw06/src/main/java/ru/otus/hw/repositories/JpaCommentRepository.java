package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCommentRepository implements CommentRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Comment> findByBookId(long bookId) {
        EntityGraph<?> entityGraph = em.getEntityGraph("Comment.withBook");
        TypedQuery<Comment> query =
                this.em.createQuery("select c from Comment c where c.book.id = :bookId", Comment.class)
                .setParameter("bookId", bookId);
        query.setHint("javax.persistence.fetchgraph", entityGraph);

        return query.getResultList();
    }

    @Override
    public Optional<Comment> findById(long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("Comment.withBook");
        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.id = :id", Comment.class)
                .setParameter("id", id);
        query.setHint("javax.persistence.fetchgraph", entityGraph);
        return query
                .getResultStream()
                .findFirst();
    }

    @Override
    public void deleteById(long id) {
        em.createQuery("delete from Comment c where c.id = :id")
                .setParameter("id", id)
                .executeUpdate();
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
