package com.pakailagi.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Kunci Rahasia untuk membuat dan memverifikasi token (Minimal 32 karakter untuk algoritma HS256)
    private static final String SECRET_KEY_STRING = "PakaiLagiSecretKeyYangSangatPanjangDanAman123!";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // Masa berlaku token (Contoh: 24 jam)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // Fungsi untuk membuat token saat user berhasil login
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Menyisipkan role (misal: USER atau ADMIN) ke dalam token
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Biasanya diisi dengan username
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Fungsi untuk mengekstrak username dari token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Fungsi untuk mengekstrak role dari token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Memeriksa apakah token sudah kedaluwarsa
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Memverifikasi apakah token tersebut valid dan cocok dengan username
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}