package com.miniboard.backend.post.repository;

import com.miniboard.backend.post.domain.PostCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostCategoryRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(PostCategory postCategory) {
        em.persist(postCategory);
    }

    public List<PostCategory> findByPostId(Long postId) {
        return em.createQuery(
                        "select pc from PostCategory pc " +
                                "join fetch pc.category " +
                                "where pc.post.id = :postId",
                        PostCategory.class
                ).setParameter("postId", postId)
                .getResultList();
    }

    public void deleteByPostIdAndCategoryId(Long postId, Long categoryId) {
        em.createQuery(
                        "delete from PostCategory pc " +
                                "where pc.post.id = :postId " +
                                "and pc.category.id = :categoryId")
                .setParameter("postId", postId)
                .setParameter("categoryId", categoryId)
                .executeUpdate();
    }

    public boolean exists(Long postId, Long categoryId) {
        Long count = em.createQuery(
                        "select count(pc) from PostCategory pc "
                                + "where pc.post.id = :postId "
                                + "and pc.category.id = :categoryId",
                        Long.class
                ).setParameter("postId", postId)
                .setParameter("categoryId", categoryId)
                .getSingleResult();

        return count > 0;
    }


}
