package com.miniboard.backend.post.repository;

import com.miniboard.backend.post.domain.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Post p) {
        em.persist(p);
    }

    public Optional<Post> findById(Long id) {
        Post post = em.find(Post.class, id);
        return Optional.ofNullable(post);
    }

    public List<Post> findAll() {
        return em.createQuery("select p from Post p order by p.id desc", Post.class)
                .getResultList();
    }

    public List<Post> findAllByUserId(Long id) {
        return em.createQuery(
                "select p from Post p where p.author.id = :userId order by p.id desc", Post.class
                )
                .setParameter("userId", id)
                .getResultList();
    }

    public void delete(Post post) {
        em.remove(post);
    }

    public void deleteById(Long id) {
        Post post = em.find(Post.class, id);

        if(post != null) {
            em.remove(post);
        }
    }

}
