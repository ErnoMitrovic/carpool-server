package de.htwsaar.carpool.service;

import de.htwsaar.carpool.domain.user.CarpoolUserDetail;
import de.htwsaar.carpool.domain.user.RegisterUserRequest;
import de.htwsaar.carpool.domain.user.TokenResponse;
import de.htwsaar.carpool.domain.user.UserRole;
import de.htwsaar.carpool.exceptions.EmailExistsException;
import de.htwsaar.carpool.exceptions.InvalidCredentialsException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    /**
     * Register a new user
     * @param registerUserRequest The request object containing the user's details
     * @throws EmailExistsException If the email is already in use
     * @return A response entity with a status code
     */
    @Transactional
    ResponseEntity<TokenResponse> registerUser(RegisterUserRequest registerUserRequest, UserRole userRole)
            throws EmailExistsException;

    /**
     * Load a user by their email
     * @param email The email of the user
     * @return The user details with the correct format for optimization
     * @throws InvalidCredentialsException If the user does not exist
     */
    CarpoolUserDetail loadUserByUsername(String email)
            throws InvalidCredentialsException;
}
