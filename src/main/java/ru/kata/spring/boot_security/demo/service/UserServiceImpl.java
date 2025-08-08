package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.UserRepo;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(long id) {
        return userRepo.findById(id);
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void delete(long id) {
        userRepo.delete(id);
    }

    @Override
    @Transactional
    public void update(User user) {
        userRepo.update(user);
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public Role findRole(String role) {
        return userRepo.findRole(role);
    }

    @Override
    @Transactional
    public void saveRole(String role) {
        userRepo.saveRole(role);
    }


}
