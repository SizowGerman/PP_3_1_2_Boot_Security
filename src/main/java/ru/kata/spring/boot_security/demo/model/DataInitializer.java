package ru.kata.spring.boot_security.demo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ROLES
        if (userService.findRole("ROLE_ADMIN") == null) {
            userService.saveRole("ROLE_USER");
        }
        if (userService.findRole("ROLE_USER") == null) {
            userService.saveRole("ROLE_ADMIN");
        }
        Set<Role> roleUserAdmin = new HashSet<>();
        roleUserAdmin.add(userService.findRole("ROLE_USER"));
        roleUserAdmin.add(userService.findRole("ROLE_ADMIN"));

        //BASE ADMIN
        if (userService.findByUsername("admin") == null) {
            User user = new User("admin", "admin@admin.com", passwordEncoder.encode("admin"),roleUserAdmin);
            userService.save(user);
        }

        Set<Role> roleUser = new HashSet<>();
        roleUser.add(userService.findRole("ROLE_USER"));
        //BASE USER
        if (userService.findByUsername("user") == null) {
            User user = new User("user", "user@user.com", passwordEncoder.encode("user"),roleUser);
        }
    }
}
