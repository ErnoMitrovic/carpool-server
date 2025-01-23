package de.htwsaar.carpool.domain.user;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record TokenResponse(@NotBlank String token) implements Serializable {
}
