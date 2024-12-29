package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.configuration.security.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();

    @Test
    void testGenerateAndValidateToken() {
        UserDetails userDetails = User.withUsername("testuser").password("password").authorities("ROLE_USER").build();
        String token = jwtUtils.generateToken(userDetails.getUsername());

        assertNotNull(token);
        assertEquals("testuser", jwtUtils.extractUsername(token));
        assertTrue(jwtUtils.isTokenValid(token, userDetails.getUsername()));
    }

    @Test
    void testTokenExpired() {
        String expiredToken = createExpiredToken();
        assertFalse(jwtUtils.isTokenValid(expiredToken, "testuser"));
    }

    private String createExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        String username = "testuser";
        long now = System.currentTimeMillis();
        // Set expiration date to a time in the past
        long expirationTime = 1000 * 60; // 1 minute in milliseconds
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(now - expirationTime * 2))
                .setExpiration(new Date(now - expirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtUtils.getSecretKey())
                .compact();
    }
}
