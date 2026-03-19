package com.scooter_backend.security.jwt;
import com.scooter_backend.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {

    private final Key key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 🔐 ACCESS TOKEN
    public String generateAccessToken(String phone) {
        return Jwts.builder()
                .setSubject(phone) // PHONE
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +1000 * 60 * 60 * 24)) // 24 soat
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔄 REFRESH TOKEN
    public String generateRefreshToken(String phone) {
        return Jwts.builder()
                .setSubject(phone)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 kun
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractPhone(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseClaims(token));
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new CustomException("Invalid token");
        }
    }

    public boolean isValid(String token, UserDetails userDetails) {
        String phone = extractPhone(token);
        return phone.equals(userDetails.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}