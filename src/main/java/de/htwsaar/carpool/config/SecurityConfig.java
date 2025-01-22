package de.htwsaar.carpool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${api.version}")
    private String apiVersion;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/api/" + apiVersion + "/auth/**",
                                        "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                //.requestMatchers(
                                  //      "/v3/api-docs/**").hasRole("DEVELOPER")
                                .anyRequest().authenticated()
                );
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