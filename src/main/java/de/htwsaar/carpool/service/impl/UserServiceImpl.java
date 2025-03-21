package de.htwsaar.carpool.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
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

import java.util.Map;

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
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setDisplayName(registerUserRequest.name())
                .setEmail(registerUserRequest.email())
                .setPassword(registerUserRequest.password())
                .setPhoneNumber(registerUserRequest.phone());
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            UserRecord userRecord = auth
                    .createUser(request);

            auth.setCustomUserClaims(userRecord.getUid(), Map.of("role", userRole.name()));

            CarpoolUser user = new CarpoolUser();
            user.setId(userRecord.getUid());
            user.setUniversityId(registerUserRequest.universityId());
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(
                    auth.createCustomToken(userRecord.getUid())
            ));
        } catch (FirebaseAuthException e) {
            throw new EmailExistsException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<TokenResponse> loginUser(String email, String password) throws InvalidCredentialsException {


        return ResponseEntity.ok(new TokenResponse(email));
    }

}
