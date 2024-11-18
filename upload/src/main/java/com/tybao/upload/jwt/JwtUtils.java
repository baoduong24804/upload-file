package com.tybao.upload.jwt;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtils {
    private static final String SECRET_KEY = "tybao";

    public static boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token); 
            return true; // Token hợp lệ
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false; // Token không hợp lệ
        }
    }

    public static Claims getTokenClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody(); // Lấy payload
    }
}

