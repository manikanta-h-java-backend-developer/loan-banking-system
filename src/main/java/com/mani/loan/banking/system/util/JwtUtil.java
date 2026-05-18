package com.mani.loan.banking.system.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <b>What is JWT and why it?</b>
 * Traditional Session Auth:
 * Login → Server creates session → Stores in memory → Returns session ID
 * Problem: Doesn't scale (multiple servers don't share memory)
 * <p>
 * JWT Auth:
 * Login → Server creates signed token → Returns token to client
 * Client sends token on every request → Server just VERIFIES signature
 * No storage needed on server → Scales perfectly
 *
 * <b>JWT Structure:</b>
 * eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW5pIiwicm9sZSI6IkFETUlOIn0.xyz123
 * ↑                          ↑                                    ↑
 * HEADER                     PAYLOAD                            SIGNATURE
 * (algorithm)              (your data/claims)                  (tamper-proof)
 */

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    // Generate signing key from secret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // Generate token from UserDetails
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey())
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Validate token against UserDetails
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Generic method to extract any claim
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }
}
