package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Component
public class JwtServiceImpl implements JwtService {
    @Value("${security.jwt.secret}")
    private String jwtSecret;
    private final long jwtExpiration = 86400000; // 24 hours
    private final String algorithm = "HmacSHA512";

    private SecretKey getSigningKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return keyFactory.generateSecret(new SecretKeySpec(keyBytes, algorithm));
    }

    public String generateToken(String email, String role)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String getEmailFromToken(String token)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        Claims claims = (Claims) Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parse(token)
                .getPayload();

        return claims.getSubject();
    }

    @Override
    public String getRoleFromToken(String token)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        Claims claims = (Claims) Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parse(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
