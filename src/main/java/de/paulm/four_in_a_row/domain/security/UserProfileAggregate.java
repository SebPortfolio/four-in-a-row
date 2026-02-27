package de.paulm.four_in_a_row.domain.security;

import de.paulm.four_in_a_row.domain.player.PlayerProfile;

public record UserProfileAggregate(User user, PlayerProfile player) {
}
