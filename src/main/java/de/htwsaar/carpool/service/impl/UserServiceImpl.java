package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.user.RegisterUserRequest;
import de.htwsaar.carpool.domain.user.TokenResponse;
import de.htwsaar.carpool.domain.user.UserRoleValue;
import de.htwsaar.carpool.exceptions.EmailExistsException;
import de.htwsaar.carpool.exceptions.InvalidCredentialsException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.repository.UserRepository;
import de.htwsaar.carpool.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public ResponseEntity<TokenResponse> registerUser(RegisterUserRequest registerUserRequest, UserRoleValue userRole)
            throws EmailExistsException {

        CarpoolUser user = new CarpoolUser();
        user.setName(registerUserRequest.name());
        user.setUniversityId(registerUserRequest.universityId());

        CarpoolUser createdUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(createdUser.getName()));
    }

    @Override
    public ResponseEntity<TokenResponse> loginUser(String email, String password) throws InvalidCredentialsException {


        return ResponseEntity.ok(new TokenResponse(email));
    }

}
