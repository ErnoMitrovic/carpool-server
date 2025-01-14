package de.htwsaar.carpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @NotNull
    @Column(name = "phone", nullable = false)
    private Integer phone;

    @NotNull
    @Column(name = "university_id", nullable = false)
    private Integer universityId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}