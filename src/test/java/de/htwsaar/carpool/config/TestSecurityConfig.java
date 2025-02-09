package de.htwsaar.carpool.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails driverUser = User.withUsername("1").password("raw").authorities("DRIVER").build();
        UserDetails basicUser = User.withUsername("2").password("raw").authorities("USER").build();

        return new InMemoryUserDetailsManager(Arrays.asList(
                driverUser, basicUser
        ));
    }
}
