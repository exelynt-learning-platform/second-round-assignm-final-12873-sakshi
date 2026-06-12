package com.ecommerce.security;

import com.ecommerce.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    // ✅ Constructor Injection
    public JwtFilter(JwtUtil jwtUtil,
                     CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("JWT FILTER RUNNING...");

        String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // 👉 Extract token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        // 👉 Validate token only if not already authenticated
        if (username != null &&
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication() == null) {

            var userDetails = userDetailsService.loadUserByUsername(username);

            if (token != null && jwtUtil.validateToken(token, userDetails.getUsername())) {

                var authToken =
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}