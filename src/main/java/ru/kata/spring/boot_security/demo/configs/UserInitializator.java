package ru.kata.spring.boot_security.demo.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;

@Component
public class UserInitializator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInitializator(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setFirstName("Fedor");
            admin.setLastName("Drozdov");
            admin.setEmail("drozd@gmail.com");
            admin.setAge(22);
            admin.setUsername("admin");                                          // USERNAME - admin
            admin.setPassword(passwordEncoder.encode("admin"));      // PASSWORD - admin
            admin.setRoles(List.of(roleAdmin, roleUser));
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user") == null) {
            User user = new User();
            user.setFirstName("Ivan");
            user.setLastName("Ivanov");
            user.setEmail("ivanov@gmail.com");
            user.setAge(25);
            user.setUsername("user");                                             // USERNAME - user
            user.setPassword(passwordEncoder.encode("user"));         // PASSWORD - user
            user.setRoles(List.of(roleUser));
            userRepository.save(user);
        }
    }
}