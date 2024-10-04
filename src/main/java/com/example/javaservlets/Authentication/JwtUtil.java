package com.example.javaservlets.Authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET_KEY = "your_secret_key"; // Change this to a strong secret key
    private static final long ADMIN_EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    private static final long STAFF_EXPIRATION_TIME = 1000 * 60 * 30; // 30 minutes

    public static String generateToken(String email, String role,String org_id,String staff_id) {
        long expirationTime = role.equals("ADMIN") ? ADMIN_EXPIRATION_TIME : STAFF_EXPIRATION_TIME;
        Map<String, Object> claims = new HashMap<>();
        claims.put("org_id", org_id);
        claims.put("role", role);

        claims.put("staff_id",staff_id);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public static String getOrgId(String token) {
        return extractClaims(token).get("org_id", String.class);
    }

    public static String getRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public static String getStaff_id(String token) {
        return extractClaims(token).get("staff_id", String.class);
    }

    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public static boolean validateToken(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }
}
