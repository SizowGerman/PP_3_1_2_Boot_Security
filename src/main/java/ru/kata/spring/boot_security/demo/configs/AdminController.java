package ru.kata.spring.boot_security.demo.configs;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

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
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());
        return "users/admin_page";
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
        return "redirect:/admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam  String name,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) Set<String> roleNames,
                             RedirectAttributes redirectAttributes) {

        if (!name.matches("^[a-zA-Zа-яА-ЯёЁ\\s\\-']+$")) {
            redirectAttributes.addFlashAttribute("error", "Invalid name format");
            return "redirect:/admin";
        }

        User user = userService.findById(id);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);

            if (password != null && !password.isBlank()) {
                user.setPassword(password);
            }

            if (roleNames != null && !roleNames.isEmpty()) {
                user.setRoleNames(roleNames);
            }

            userService.update(user);
        }

        return "redirect:/admin";

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

}