package com.miniboard.backend.post.repository;

import com.miniboard.backend.post.domain.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Category category) {
        em.persist(category);
    }

    public Optional<Category> findById(Long id) {
        Category category = em.find(Category.class, id);
        return Optional.ofNullable(category);
    }

    public List<Category> findAll() {
        return em.createQuery(
                        "select c from Category c", Category.class
                )
                .getResultList();
    }

    public void delete(Category category) {
        em.remove(category);
    }

    public void deleteById(Long id) {
        Category category = em.find(Category.class, id);

        if(category != null) {
            em.remove(category);
        }
    }

    public boolean existsByUserIdAndName(Long userId, String name) {
        Long count = em.createQuery(
                        "select count(c) from Category c where c.user.id = :userId and c.name = :name",
                        Long.class
                ).setParameter("userId", userId)
                .setParameter("name", name)
                .getSingleResult();

        return count > 0;
    }

}
