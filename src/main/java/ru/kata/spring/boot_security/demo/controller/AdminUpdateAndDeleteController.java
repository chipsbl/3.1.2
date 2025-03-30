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
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminUpdateAndDeleteController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminUpdateAndDeleteController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    //Обновление пользователя
    @GetMapping("/update")
    public String editUser(@RequestParam Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("user", user);
        return "update";
    }

    //Отправка формы обновления
    @PostMapping("/update")
    public String updateUser(@RequestParam Long id, @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             @RequestParam(name = "selectedRoles", required = false) List<Long> selectedRoleIds,
                             Model model) {
        if (bindingResult.getFieldErrors()
                .stream()
                .anyMatch(fieldError -> !fieldError.getField().equals("password")) || !user.getPassword().isEmpty()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            model.addAttribute("selectedRoleIds", selectedRoleIds);
            return "update";
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
        userService.update(user);
        return "redirect:/admin";
    }

    //Окно всплытия удаление пользователя
    @GetMapping("/delete")
    public String deleteUserWindow(@RequestParam Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "delete";
    }

    //Удаление пользователя
    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
