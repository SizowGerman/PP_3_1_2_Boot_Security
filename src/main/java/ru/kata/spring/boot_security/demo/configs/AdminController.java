package ru.kata.spring.boot_security.demo.configs;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PasswordEncoder passwordEncoder;
    private UserService userService;

    @Autowired
    public AdminController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());
        return "users/all_users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin_page")
    public String adminPage(Model model, @AuthenticationPrincipal User UserDetails) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", UserDetails);
        model.addAttribute("activePage", "admin");
        return "users/admin_panel";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute User user,
                          BindingResult bindingResult,
                          Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "users/all_users";
        }
        userService.save(user);
        return "redirect:/admin_panel";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public String updateUser(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) List<String> roleNames, // <- changed from Set<String>
            RedirectAttributes redirectAttributes) {

        System.out.println(">>> /update called, id=" + id + ", name=" + name + ", email=" + email + ", roles=" + roleNames);

        if (!name.matches("^[a-zA-Zа-яА-ЯёЁ\\s\\-']+$")) {
            redirectAttributes.addFlashAttribute("error", "Invalid name format");
            return "redirect:/admin_panel";
        }

        User user = userService.findById(id);

        if (user != null) {
            user.setName(name);
            user.setEmail(email);

            if (password != null && !password.isBlank()) {
                user.setPassword(password);
            }

            // Convert list to set and save roles
            if (roleNames != null && !roleNames.isEmpty()) {
                user.setRoleNames(new HashSet<>(roleNames));
            } else {
                user.setRoleNames(new HashSet<>(Collections.singletonList("ROLE_USER")));
            }

            userService.update(user);
        }

        return "redirect:/admin_panel";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/admin_panel";
    }

}