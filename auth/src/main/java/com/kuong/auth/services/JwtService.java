package com.kuong.auth.services;

import com.kuong.auth.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final long accessExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.accessExpiration = accessExpiration;
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .claim("tokenVersion", user.getTokenVersion())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        String type = extractAllClaims(token).get("type", String.class);
        return "refresh".equals(type);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject().equals(user.getUsername())
                && claims.getExpiration().after(new Date())
                && claims.get("tokenVersion", Integer.class).equals(user.getTokenVersion());
    }


}
