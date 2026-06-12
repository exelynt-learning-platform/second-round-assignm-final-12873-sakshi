package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    @Value("${app.default-balance}")
    private double defaultBalance;

    @Value("${app.password.min-length:8}")
    private int minPasswordLength;

    public AuthService(UserRepository repo, JwtUtil jwtUtil, PasswordEncoder encoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    // ✅ REGISTER
    public User register(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        // 🔥 NORMALIZE INPUT (IMPORTANT)
        String username = user.getUsername().trim().toLowerCase();
        String password = user.getPassword().trim();

        // 🔥 duplicate check
        if (repo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // 🔐 PASSWORD VALIDATION
        if (password.length() < minPasswordLength ||
                password.length() > 128 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[0-9].*") ||
                !password.matches(".*[@#$%^&+=].*")) {

            throw new RuntimeException(
                    "Password must be strong (min length, uppercase, number, special char)");
        }

        // 🔐 encode password
        user.setPassword(encoder.encode(password));

        // 🔥 set normalized username
        user.setUsername(username);

        // 🔥 ROLE HANDLING
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // 🔥 WALLET LOGIC
        if ("USER".equalsIgnoreCase(user.getRole())) {
            user.setAmount(defaultBalance);
        } else {
            user.setAmount(0);
        }

        return repo.save(user);
    }

    // ✅ LOGIN
    public String login(String username, String password) {

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password cannot be null");
        }

        String normalizedUsername = username.trim().toLowerCase();

        User user = repo.findByUsername(normalizedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password.trim(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(normalizedUsername);
    }
}