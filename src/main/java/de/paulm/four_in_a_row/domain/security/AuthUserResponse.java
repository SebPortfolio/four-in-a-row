package de.paulm.four_in_a_row.domain.security;

public record AuthUserResponse(
        String accessToken,
        String refreshToken,
        UserProfileAggregate userContext) {
}
