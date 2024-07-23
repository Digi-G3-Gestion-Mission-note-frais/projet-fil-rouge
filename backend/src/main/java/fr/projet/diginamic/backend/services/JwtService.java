package fr.projet.diginamic.backend.services;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Service for handling JWT */
@Service
public class JwtService {
    /** Secret key String for JWT */
    @Value("${jwt.secret}")
    private String secretKey;

    /** Validity for JWT */
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30); // 30 minutes

    /**
     * Generate JWT
     * 
     * @param userDetails - the user details
     * @param role        - the role of the user
     */
    public String generateToken(UserDetails userDetails, String role) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims).subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(getSecretKey()).compact();
    }

    /**
     * Refresh by generating a new JWT token
     *
     * @param oldToken - the old JWT token
     */
    public String refreshToken(String oldToken) {
        Claims oldClaims = getClaims(oldToken);
        String username = oldClaims.getSubject();
        Map<String, String> roleClaim = new HashMap<>();

        roleClaim.put("role", oldClaims.get("role", String.class));

        return Jwts.builder()
                .claims(roleClaim).subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(getSecretKey()).compact();
    }

    /** Get secret key object */
    private SecretKey getSecretKey() {
        byte[] key = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    /**
     * Extract user with email from access token
     * 
     * @param jwtToken - acces token
     */
    public String extractUsername(String jwtToken) {
        Claims claims = getClaims(jwtToken);
        return claims.getSubject();
    }

    /**
     * Get claims from JWT token
     * 
     * @param jwtToken - the JWT token
     */
    private Claims getClaims(String jwtToken) {
        Claims claims = Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(jwtToken).getPayload();
        return claims;
    }

    /**
     * Check if token expiration is still valid
     * 
     * @param jwtToken - the JWT token
     */
    public boolean isTokenExpirationValid(String jwtToken) {
        Claims claims = getClaims(jwtToken);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}
