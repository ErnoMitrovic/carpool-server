package de.htwsaar.carpool.service;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface JwtService {
    /**
     * The jwt with the email and the different roles that a user can have
     * @param email user's email
     * @param roles the different roles that a user can have
     * @return The jwt in a String format
     */
    String generateToken(String email, Collection<String> roles) ;
    String getEmailFromToken(String token);
    String getRoleFromToken(String token);
    boolean validateToken(String token);
}
