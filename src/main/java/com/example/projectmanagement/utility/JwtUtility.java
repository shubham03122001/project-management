package com.example.projectmanagement.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtUtility {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long  jwtExpiration;

    private Key signingKey;

    @PostConstruct
    public void init(){
        signingKey   = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String email){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime()+jwtExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();

    }

    public String extractUsername(String jwtToken){
        return Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token , UserDetails userDetails){
        final String username = extractUsername(token);
        return  username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }

    public boolean isTokenExpired(String token){
        Date expiration = Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date());
    }

}
