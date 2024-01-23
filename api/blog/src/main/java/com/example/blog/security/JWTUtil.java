package com.example.blog.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    private static final String KEY = "minhaChaveSecreta";

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora de validade
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        return Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody().getSubject().equals(username);
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody().getSubject();
    }
}
