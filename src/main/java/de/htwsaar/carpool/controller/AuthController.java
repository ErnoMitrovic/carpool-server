package de.htwsaar.carpool.controller;

import de.htwsaar.carpool.domain.user.LoginRequest;
import de.htwsaar.carpool.domain.user.RegisterUserRequest;
import de.htwsaar.carpool.domain.user.TokenResponse;
import de.htwsaar.carpool.domain.user.UserRole;
import de.htwsaar.carpool.service.AuthService;
import de.htwsaar.carpool.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/${api.version}/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Operation(summary = "Register a driver to the system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204"
            )
    })
    @PostMapping("/driver/")
    public ResponseEntity<TokenResponse> registerDriver(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        return userService.registerUser(registerUserRequest, UserRole.DRIVER);
    }

    @Operation(summary = "Get a json web token")
    @PostMapping("/token/")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return authService.login(loginRequest.email(), loginRequest.password());
    }
}
