package com.simon.smile.system;

import com.simon.smile.user.AppUser;
import com.simon.smile.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.simon.smile.user.Roles.ROLE_ADMIN;
import static com.simon.smile.user.Roles.ROLE_USER;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    private final UserService userService;

    public DatabaseInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userService.findByUsername("admin").isPresent())
            return;
        var admin = new AppUser()
                .setUsername("admin")
                .setNickname("Administrator")
                .setEmail("admin@example.com")
                .setPassword("PassW0rd")
                .setEnabled(true)
                .setRoles(ROLE_ADMIN.getRole());
        var invalidUser = new AppUser()
                .setUsername("invalid")
                .setNickname("Invalid User")
                .setEmail("invalid@example.com")
                .setPassword("PassW0rd")
                .setEnabled(false)
                .setRoles(ROLE_USER.getRole());
        userService.create(admin);
        userService.create(invalidUser);
    }
}
