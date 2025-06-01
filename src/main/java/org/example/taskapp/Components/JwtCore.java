package org.example.taskapp.Components;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.taskapp.Security.UserDetailsImp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtCore {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifeTime}")
    private long accessTokenLifeTime;

    @Value("${jwt.refreshLifeTime}")
    private long refreshTokenLifeTime;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication){
        UserDetailsImp userDetailsImp = (UserDetailsImp) authentication.getPrincipal();
        return generateTokenFromUsername(userDetailsImp.getUsername(), accessTokenLifeTime);
    }

    public String generateTokenFromUsername(String username) {
        return generateTokenFromUsername(username, accessTokenLifeTime);
    }

    public String generateRefreshToken(String username) {
        return generateTokenFromUsername(username, refreshTokenLifeTime);
    }

    private String generateTokenFromUsername(String username, long lifeTime) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + lifeTime);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationTime() {
        return System.currentTimeMillis() + accessTokenLifeTime;
    }
}
