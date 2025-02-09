package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.user.RegisterUserRequest;
import de.htwsaar.carpool.domain.user.TokenResponse;
import de.htwsaar.carpool.domain.user.UserRoleValue;
import de.htwsaar.carpool.exceptions.EmailExistsException;
import de.htwsaar.carpool.exceptions.InvalidCredentialsException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.model.UserRole;
import de.htwsaar.carpool.repository.RoleRepository;
import de.htwsaar.carpool.repository.UserRepository;
import de.htwsaar.carpool.service.JwtService;
import de.htwsaar.carpool.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    @Override
    public ResponseEntity<TokenResponse> registerUser(RegisterUserRequest registerUserRequest, UserRoleValue userRole)
            throws EmailExistsException {
        if (userRepository.existsByEmail(registerUserRequest.email())) {
            throw new EmailExistsException("Email already exists");
        }

        CarpoolUser user = new CarpoolUser();
        user.setEmail(registerUserRequest.email());
        user.setPassword(passwordEncoder.encode(registerUserRequest.password()));
        user.setName(registerUserRequest.name());
        user.setPhone(registerUserRequest.phone());
        user.setUniversityId(registerUserRequest.universityId());
        UserRole role = roleRepository.findByName(userRole.name());
        user.setRole(role);

        CarpoolUser createdUser = userRepository.save(user);

        String jwt = jwtService.generateToken(
                createdUser.getId(),
                Set.of(createdUser.getRole().getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(jwt));
    }

    @Override
    public ResponseEntity<TokenResponse> loginUser(String email, String password) throws InvalidCredentialsException {
        CarpoolUser user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String jwt = jwtService.generateToken(
                user.getId(),
                Set.of(user.getRole().getName()));
        return ResponseEntity.ok(new TokenResponse(jwt));
    }

}
