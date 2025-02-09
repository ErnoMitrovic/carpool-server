package de.htwsaar.carpool.config;

import de.htwsaar.carpool.exceptions.InvalidCredentialsException;
import de.htwsaar.carpool.model.CarpoolUser;
import de.htwsaar.carpool.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/", "/api/{version}/auth/**",
                                        "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                //.requestMatchers(
                                  //      "/v3/api-docs/**").hasRole("DEVELOPER")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService);
        return http.build();
    }

    /**
     * A good password encoder to use
     * In production use 12> strength
     * @return A password encoder
     */
    @Bean
    @Profile("prod")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @Profile({"dev", "test"})
    public PasswordEncoder devPasswordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Profile({"dev", "prod"})
    @Bean
    public UserDetailsService userDetailsService(UserRepository repository) {
        return email -> {
            CarpoolUser carpoolUser = repository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);
            return User.withUsername(String.valueOf(carpoolUser.getId()))
                    .authorities(carpoolUser.getRole().getName())
                    .accountExpired(!carpoolUser.getIsActive())
                    .credentialsExpired(!carpoolUser.getIsActive())
                    .disabled(!carpoolUser.getIsActive())
                    .accountLocked(!carpoolUser.getIsActive()).build();
        };
    }
}