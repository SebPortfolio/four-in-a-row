package de.paulm.four_in_a_row.domain.security;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER(Set.of(
            Permission.USER_VIEW_SELF,
            Permission.USER_WRITE_SELF,
            Permission.SESSION_VIEW,
            Permission.SESSION_DELETE,
            Permission.PLAYER_VIEW,
            Permission.PLAYER_WRITE,
            Permission.PLAYER_STATS_VIEW,
            Permission.GAME_VIEW,
            Permission.GAME_CREATE,
            Permission.GAME_PLAY,
            Permission.GAME_SURRENDER)),

    ROLE_ADMIN(Set.of(
            Permission.USERS_ADMIN,
            Permission.USERS_VIEW,
            Permission.GAME_DELETE)),

    ROLE_LUCIFER(Set.of(Permission.values())); // god mode

    private final Set<Permission> permissions;
}