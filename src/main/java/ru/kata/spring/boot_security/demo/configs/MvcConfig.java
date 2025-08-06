package ru.kata.spring.boot_security.demo.configs;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/users")
public class MvcConfig {

    private UserService userService;

    @Autowired
    public MvcConfig(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());
        return "users/list";
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute User user,
                          BindingResult bindingResult,
                          Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "users/list";
        }

        userService.save(user);
        return "redirect:/users";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam  String name,
                             @RequestParam String email,
                             RedirectAttributes redirectAttributes) {

        if (!name.matches("^[a-zA-Zа-яА-ЯёЁ\\s\\-']+$")) {
            redirectAttributes.addFlashAttribute("error", "Invalid name format");
            return "redirect:/users";
        }

        User user = userService.findById(id);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            userService.update(user);
        }

        return "redirect:/users";

    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/users";
    }

}
