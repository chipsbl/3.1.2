package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserDetailServiceImpl;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminStartAndSaveController {

    private final UserDetailServiceImpl userdetailServiceImpl;
    private final RoleService roleService;

    public AdminStartAndSaveController(UserDetailServiceImpl userService, RoleService roleService) {
        this.userdetailServiceImpl = userService;
        this.roleService = roleService;
    }

    //Страница со всеми пользователями
    @GetMapping()
    public String home(Model model) {
        model.addAttribute("users", userdetailServiceImpl.getAll());
        return "users";
    }

    //Форма создания пользователя
    @GetMapping("/save")
    public String saveUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "save";
    }

    //Отправка формы
    @PostMapping()
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                           @RequestParam(name = "selectedRoles", required = false) List<Long> selectedRoleIds,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            model.addAttribute("selectedRoles", selectedRoleIds);
            return "save";
        }
        Collection<Role> roles;
        if (selectedRoleIds == null || selectedRoleIds.isEmpty()) {
            roles = Collections.singletonList(roleService.findByName("ROLE_USER"));
        } else {
            roles = selectedRoleIds.stream()
                    .map(roleService::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        user.setRoles(roles);
        userdetailServiceImpl.save(user);
        return "redirect:/admin";
    }
}