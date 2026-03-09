package com.miniboard.backend.comment.repository;

import com.miniboard.backend.comment.domain.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Comment comment) {
        em.persist(comment);
    }

    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    public List<Comment> findAll() {
        return em.createQuery("select c from Comment c order by c.id", Comment.class)
                .getResultList();
    }

    public List<Comment> findByPostId(Long postId) {
        return em.createQuery(
                        "select c from Comment c " +
                                "join fetch c.author " +
                                "left join fetch c.parent " +
                                "where c.post.id = :postId " +
                                "order by c.createdAt asc"
                        , Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }

    public void deleteById(Long id) {
        Comment comment = em.find(Comment.class, id);

        if(comment != null) {
            em.remove(comment);
        }
    }
}