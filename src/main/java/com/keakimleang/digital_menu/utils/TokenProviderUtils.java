package com.keakimleang.digital_menu.utils;

import com.keakimleang.digital_menu.configs.properties.*;
import com.keakimleang.digital_menu.features.users.payloads.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import java.lang.SecurityException;
import java.time.*;
import java.util.*;
import javax.crypto.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProviderUtils {
    @Value("${token.issuer}")
    private String issuer;
    @Value("${token.accessTokenExpiresHours}")
    private Long accessExpiration;
    @Value("${token.refreshTokenExpiresHours}")
    private Long refreshExpiration;
    @Value("${token.secret}")
    private String secret;

    private final TokenProperties tokenProperties;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(CustomUserDetails ud) {
        return generateToken(ud, accessExpiration);
    }

    public String generateRefreshToken(CustomUserDetails ud) {
        return generateToken(ud, refreshExpiration);
    }

    public String generateToken(CustomUserDetails ud, Long expiration) {
        String username = ud.getUsername();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + expiration * 3600000);
        SecretKey key = getSecretKey();
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = getSecretKey();
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT token compact of handler are invalid.");
        }
    }

    public boolean isTokenNotExpired(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Instant getExpirationDateFromToken(String accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
            return claims.getExpiration().toInstant();
        } catch (Exception e) {
            return null;
        }
    }
}
