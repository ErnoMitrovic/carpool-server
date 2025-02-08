package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;

@Component
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final SecretKey key = Jwts.SIG.HS512.key().build();
    private final String AUTHORITIES_KEY = "roles";

    @Override
    public String generateToken(Long id, Collection<String> roles) {
        // 24 hours
        long jwtExpiration = 86400000;

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(String.valueOf(id))
                .signWith(key)
                .claims()
                .add(AUTHORITIES_KEY, roles)
                .and()
                .issuedAt(now)
                .expiration(validity)
                .compact();
    }

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = (Claims) Jwts.parser()
                .verifyWith(key)
                .build()
                .parse(token)
                .getPayload();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils
                .commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(this.key).build().isSigned(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            log.error("Invalid JWT token: {}", exception.getMessage());
            log.trace("Invalid JWT token trace.", exception);
        }
        return false;
    }
}
