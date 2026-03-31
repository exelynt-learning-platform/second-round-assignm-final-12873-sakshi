package com.ecommerce.config;

import com.ecommerce.security.JwtFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        System.out.println("SECURITY CONFIG LOADED");

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // 🔓 PUBLIC APIs
                        .requestMatchers("/api/auth/**").permitAll()

                        // 🛒 CART → ONLY USER
                        .requestMatchers("/api/cart/**").hasRole("USER")

                        // 🧾 ORDER → ONLY USER
                        .requestMatchers("/api/orders/**").hasRole("USER")

                        // 💳 PAYMENT → ONLY USER  🔥 ADD THIS
                        .requestMatchers("/api/payment/**").hasRole("USER")

                        // 📦 PRODUCTS → READ (USER + ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyRole("USER", "ADMIN")

                        // 📦 PRODUCTS → WRITE (ADMIN ONLY)
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // 🔒 ALL OTHER APIs
                        .anyRequest().authenticated()
                )

                // 🔐 JWT FILTER
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}