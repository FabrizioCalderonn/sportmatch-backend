package org.ncapas.canchitas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    //  Usa mínimo 32 bytes (HS256)
    private static final Key SECRET = Keys.hmacShaKeyFor(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30".getBytes());

    private static final long EXP_MS = 1000 * 60 * 60 * 4;   // 4 h

    /** Generar token JWT */
    public String generate(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(SECRET, SignatureAlgorithm.HS256)   // ← API 0.11
                .compact();
    }

    /** Extraer usuario (subject) */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()                // ← API 0.11
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Validar firma y expiración */
    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET)
                    .build()
                    .parseClaimsJws(token);            // lanza excepción si está mal
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
