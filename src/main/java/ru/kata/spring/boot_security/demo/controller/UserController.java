package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.security.Principal;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Страница просмотра для USER
    @GetMapping("/user")
    public String home(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user";
    }

    //Страница просмотра всех юзеров для админа
    @GetMapping("/admin/user/{username}")
    public String admin(Model model, Principal principal, @PathVariable String username) {
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        return "user";
    }
}
