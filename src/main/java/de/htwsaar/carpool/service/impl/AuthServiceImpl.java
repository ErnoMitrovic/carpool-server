package de.htwsaar.carpool.service.impl;

import de.htwsaar.carpool.domain.user.CarpoolUserDetail;
import de.htwsaar.carpool.domain.user.TokenResponse;
import de.htwsaar.carpool.service.AuthService;
import de.htwsaar.carpool.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationConfiguration authenticationConfiguration, JwtService jwtService) throws Exception {
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
        this.jwtService = jwtService;
    }

    @Override
    public ResponseEntity<TokenResponse> login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        CarpoolUserDetail userDetails = (CarpoolUserDetail) authentication.getPrincipal();
        String jwt = jwtService.generateToken(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );

        return ResponseEntity.ok(new TokenResponse(jwt));
    }
}
