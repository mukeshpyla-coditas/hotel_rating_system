package com.mukesh.auth_server.util;

import com.mukesh.auth_server.custom.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    private final CustomUserDetailsService customUserDetailsService;
    private final String secret;
    private final Integer expirationTime;
    private final String issuer;

    JwtUtil(
            @Value("${jwt.expiration-minutes}")
            Integer expirationTime,
            @Value("${jwt.secret}")
            String secret,
            @Value("${jwt.issuer}")
            String issuer,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.expirationTime = expirationTime;
        this.secret = secret;
        this.issuer = issuer;
        this.customUserDetailsService = customUserDetailsService;
    }

    public String generateJwtToken(String username, Long userId, String role) {
        return Jwts.builder()
                .claims()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ((long) expirationTime * 60 * 1000)))
                .issuer(issuer)
                .and()
                .claim("userId", userId)
                .claim("role", role)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        String role = String.valueOf(extractClaims(token).get("role"));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(extractUsername(token));
        return userDetails.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
