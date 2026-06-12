package com.ecommerce.controller;

import com.ecommerce.entity.User;
import com.ecommerce.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // 🔥 REGISTER
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        service.register(user);

        return "User registered successfully";
    }

    // 🔥 LOGIN
    @PostMapping("/login")
    public String login(@RequestBody User user) {

        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Invalid login request");
        }

        return service.login(user.getUsername(), user.getPassword());
    }
}