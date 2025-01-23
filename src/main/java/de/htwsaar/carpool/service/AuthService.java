package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.user.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public interface AuthService {
    /**
     * Logs in a user with the login request
     * @return A response entity with the json web token
     */
    ResponseEntity<TokenResponse> login(String email, String password) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
