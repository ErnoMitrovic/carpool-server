package de.htwsaar.carpool.service;

import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public interface JwtService {
    String generateToken(String email, String role) throws NoSuchAlgorithmException, InvalidKeySpecException;
    String getEmailFromToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException;
    String getRoleFromToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException;
    boolean validateToken(String token);
}
