package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository repo, JwtUtil jwtUtil, PasswordEncoder encoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    // ✅ REGISTER
    public User register(User user) {

        // 🔥 check duplicate user
        if (repo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        // 🔥 ROLE HANDLING
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // default
        }

        // 🔥 WALLET LOGIC
        if (user.getRole().equalsIgnoreCase("USER")) {
            user.setAmount(10000); // only USER gets balance
        } else {
            user.setAmount(0); // ADMIN or others
        }

        return repo.save(user);
    }

    // ✅ LOGIN
    public String login(String username, String password) {

        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(username);
    }
}