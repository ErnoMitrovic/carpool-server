package de.htwsaar.carpool.domain.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
public class CarpoolUserDetail implements UserDetails {
    private String password;
    private String username;
    @Getter
    private Long id;
    private Set<GrantedAuthority> grantedAuthorities;

    public void setRoles(String ... roles) {
        this.grantedAuthorities = Arrays.stream(roles).map(role -> {
            Assert.isTrue(!role.startsWith("ROLE_"), () ->
                    role + " cannot start with ROLE_ (it is automatically added)");

            return new SimpleGrantedAuthority( "ROLE_" + role);
        }).collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
