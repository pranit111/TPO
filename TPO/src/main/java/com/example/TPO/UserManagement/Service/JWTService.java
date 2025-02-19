package com.example.TPO.UserManagement.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private final Key secretKey; // Securely generated key
    private static final long TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 10 hours

    // Constructor to initialize the secret key
    public JWTService() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Secure key generation
    }


    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // ✅ Store user ID in JWT
        return createToken(claims, username);
    }

    // Generate JWT Token with UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Method to create a JWT token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Set custom claims
                .setSubject(subject) // Set username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Current timestamp
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY)) // Token validity
                .signWith(secretKey, SignatureAlgorithm.HS256) // Signing with HS256 and secret key
                .compact();
    }
    // Extract User ID from JWT token
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return Long.parseLong(claims.get("userId").toString()); // Get userId from claims
    }


    // Extract username from the token
    public String extractUser(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract specific claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey) // Use the signing key to parse
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validate the token with the user details
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUser(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract token expiration date
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Set authentication context for the user
    public void setAuthenticationContext(String token, UserDetails userDetails) {
        var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
