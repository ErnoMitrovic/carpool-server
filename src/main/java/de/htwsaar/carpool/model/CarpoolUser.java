package de.htwsaar.carpool.model;

import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "carpool_user", uniqueConstraints = {
        @UniqueConstraint(name = "user_ak_1", columnNames = {"email"}),
        @UniqueConstraint(name = "user_ak_2", columnNames = {"phone"})
})
@SQLDelete(sql = "UPDATE carpool_user SET is_active = false WHERE id=?")
@SQLRestriction("is_active=true")
@SequenceGenerator(name = "carpool_user_id_seq", sequenceName = "carpool_user_id_seq", allocationSize = 1)
public class CarpoolUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carpool_user_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Email
    @Size(max = 50)
    @NotNull
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "phone", nullable = false)
    private Integer phone;

    @Column(name = "university_id")
    private Integer universityId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

}