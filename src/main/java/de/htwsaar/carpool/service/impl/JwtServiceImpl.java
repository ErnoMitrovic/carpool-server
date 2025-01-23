package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.user.PrincipalUser;
import de.htwsaar.carpool.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class JwtServiceImpl implements JwtService {
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(Long id, String email, Collection<String> roles) {
        // 24 hours
        long jwtExpiration = 86400000;

        return Jwts.builder()
                .subject(String.valueOf(id))
                .signWith(getSecretKey())
                .claims()
                .add("email", email)
                .add("roles", roles)
                .and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .compact();
    }

    @Override
    public PrincipalUser getUserFromToken(String token) {
        Claims claims = (Claims) Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parse(token)
                .getPayload();

        return new PrincipalUser(
                Integer.parseInt(claims.getSubject()),
                claims.get("email", String.class),
                (String) claims.get("roles", List.class).get(0)
        );
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
