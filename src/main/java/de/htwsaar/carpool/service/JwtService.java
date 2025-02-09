package de.htwsaar.carpool.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface JwtService {
    /**
     * The jwt with the email and the different roles that a user can have
     * @param id user's id
     * @param roles the different roles that a user can have
     * @return The jwt in a String format
     */
    String generateToken(Long id, Collection<String> roles) ;
    Authentication getAuthentication(String token);
    boolean validateToken(String token);
}
