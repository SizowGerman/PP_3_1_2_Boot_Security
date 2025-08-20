package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.UserRepo;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder;
    private UserRepo userRepo;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.passwordEncoder = passwordEncoder;
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

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        Set<Role> roles = user.getRoleNames().stream()
                .map(roleName -> userRepo.findRole(roleName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        user.setRoles(roles);

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

        User oldUser = userRepo.findById(user.getId());
        if (oldUser == null) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        System.out.println("Updating user: " + oldUser);


        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            // CHECK for encoded Bcrypt
            if (user.getPassword().length()!=60) {
                oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        // Always ROLE_USER
        Set<Role> roles = new HashSet<>();
        roles.add(userRepo.findRole("ROLE_USER"));

        // Add ROLE_ADMIN if checkbox
        if (user.getRoleNames() != null && user.getRoleNames().contains("ROLE_ADMIN")) {
            roles.add(userRepo.findRole("ROLE_ADMIN"));
        }

        oldUser.setRoles(roles);
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        userRepo.save(oldUser);
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
