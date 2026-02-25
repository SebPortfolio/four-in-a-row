package de.paulm.four_in_a_row.domain.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    // User / Auth
    USER_VIEW_SELF("user.account.view-self"),
    USER_WRITE_SELF("user.account.write-self"),
    USER_ADMIN("user.account.admin"),

    // Sessions
    SESSION_VIEW("user.session.view"),
    SESSION_DELETE("user.session.delete"),

    // Player Profile
    PLAYER_VIEW("player.profile.view"),
    PLAYER_WRITE("player.profile.write"),
    PLAYER_STATS_VIEW("player.stats.view"),

    // Game Logic
    GAME_VIEW("game.match.view"),
    GAME_CREATE("game.match.create"),
    GAME_PLAY("game.match.play"),
    GAME_SURRENDER("game.match.surrender"),
    GAME_DELETE("game.delete");

    private final String value;
}