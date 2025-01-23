package de.htwsaar.carpool.config;

import de.htwsaar.carpool.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/", "/api/{version}/auth/**",
                                        "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                //.requestMatchers(
                                  //      "/v3/api-docs/**").hasRole("DEVELOPER")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userService);
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
    @Profile("!prod")
    public PasswordEncoder devPasswordEncoder() {
        return new BCryptPasswordEncoder(4);
    }
}