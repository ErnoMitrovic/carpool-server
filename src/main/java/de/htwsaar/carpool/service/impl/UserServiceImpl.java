package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.user.RegisterUserRequest;
import de.htwsaar.carpool.domain.user.UserRole;
import de.htwsaar.carpool.exceptions.EmailExistsException;
import de.htwsaar.carpool.exceptions.InvalidCredentialsException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.model.Role;
import de.htwsaar.carpool.repository.RoleRepository;
import de.htwsaar.carpool.repository.UserRepository;
import de.htwsaar.carpool.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public ResponseEntity<Void> registerUser(RegisterUserRequest registerUserRequest, UserRole userRole)
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
        Role role = roleRepository.findByName(userRole.name());
        user.setRole(role);

        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws InvalidCredentialsException {
        CarpoolUser user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new InvalidCredentialsException("Invalid email or password")
                );

        return User.builder()
                .passwordEncoder(passwordEncoder::encode)
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }

}
