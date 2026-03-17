package de.paulm.four_in_a_row.domain.security;

public record AuthResponse(
        String accessToken,
        String refreshToken) {
}
