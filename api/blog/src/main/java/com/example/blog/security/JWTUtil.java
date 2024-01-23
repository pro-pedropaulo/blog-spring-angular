package com.example.blog.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    private static final String KEY = "minhaChaveSecreta";

    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000)) // 1 hora de validade
                .sign(Algorithm.HMAC512(KEY));
    }

    public boolean validateToken(String token, String username) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(KEY)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject().equals(username);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(KEY)).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
