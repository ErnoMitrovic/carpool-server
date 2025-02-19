package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Entity
@Table(name = "carpool_role", uniqueConstraints = {
        @UniqueConstraint(name = "role_ak_1", columnNames = {"name"})
})
@SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1)
public class UserRole implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}