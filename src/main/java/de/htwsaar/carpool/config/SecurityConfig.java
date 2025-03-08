package de.htwsaar.carpool.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    /**
     * Firebase Auth bean
     * @return Firebase Auth instance
     * @throws IOException If the credentials are invalid
     */
    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        if(FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
            return FirebaseAuth.getInstance(firebaseApp);
        }

        return FirebaseAuth.getInstance();
    }

    @Bean
    @Profile("!test")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/", "/chat/**", "/api/{version}/auth/**",
                                        "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                //.requestMatchers(
                                //      "/v3/api-docs/**").hasRole("DEVELOPER")
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        return http.build();
    }

    /**
     * A good password encoder to use
     * In production use 12> strength
     *
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
}