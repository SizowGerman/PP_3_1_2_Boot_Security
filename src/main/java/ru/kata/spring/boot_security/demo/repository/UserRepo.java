package ru.kata.spring.boot_security.demo.repository;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

@Repository
public class UserRepo {

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findAll() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

    public void save(User user) {
        entityManager.persist(user);
    }

    public User findById(long id) {
        return entityManager.find(User.class, id);
    }

    public void update(User user) {
        entityManager.merge(user);
    }

    public void delete(long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    public void saveRole(String role) {
        Role roleObj = new Role(role);
        entityManager.persist(roleObj);
    }

    public User findByUsername(String username) {
        try {
            Query query = entityManager.createQuery("from User u where u.name = :username");
            query.setParameter("username", username);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Role findRole(String role) {
        Query query = entityManager.createQuery("from Role r where r.role = :role");
        query.setParameter("role", role);
        return (Role) query.getSingleResult();
    }

}