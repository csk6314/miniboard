package com.miniboard.backend.member.repository;

import com.miniboard.backend.member.domain.UserEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public void save(UserEntity user) {
        em.persist(user);
    }

    public Optional<UserEntity> findById(Long id) {
           UserEntity user = em.find(UserEntity.class, id);
           return Optional.ofNullable(user);
    }

    public Optional<UserEntity> findByEmail(String email) {
        List<UserEntity>  result = em.createQuery("select u from User u where u.email = :email",UserEntity.class)
                .setParameter("email",email)
                .getResultList();

        return result.stream().findFirst();
    }

    public List<UserEntity> findAll() {
        return em.createQuery("select u from User u", UserEntity.class)
                .getResultList();
    }

    public void deleteById(Long id) {
        UserEntity user = em.find(UserEntity.class, id);

        if(user != null) {
            em.remove(user);
        }
    }


}
