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

    public AuthService(UserRepository repo, JwtUtil jwtUtil, PasswordEncoder encoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    // ✅ REGISTER
    public User register(User user) {

        // 🔥 NULL CHECK (extra safety)
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }

        // 🔥 duplicate check
        if (repo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // 🔐 PASSWORD VALIDATION
        String password = user.getPassword();

        if (password == null ||
                password.length() < 8 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[0-9].*") ||
                !password.matches(".*[@#$%^&+=].*")) {

            throw new RuntimeException(
                    "Password must be strong (8+ chars, uppercase, number, special char)");
        }

        // 🔐 encode password
        user.setPassword(encoder.encode(password));

        // 🔥 ROLE HANDLING (safe compare)
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

        // 🔥 NULL CHECK
        if (username == null || password == null) {
            throw new RuntimeException("Username or password cannot be null");
        }

        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(username);
    }
}