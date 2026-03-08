package de.paulm.four_in_a_row.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdministrationService {

    private final UserService userService;
    private final UserProfileAggregateService userProfileAggregateService;
    private final PlayerProfileService playerProfileService;

    @Transactional(readOnly = true)
    public List<UserAdminResponse> getUsersAsAdmin() {
        List<UserProfileAggregate> userAggregates = userProfileAggregateService.getAllAggregates();
        List<UserAdminResponse> responseList = new ArrayList<>();
        userAggregates.stream().forEach(aggregate -> {
            responseList.add(buildResponse(aggregate.user(), aggregate.player().getDisplayName()));
        });
        return responseList;
    }

    @Transactional(readOnly = true)
    public UserAdminResponse getUserByIdAsAdmin(Long userId) {
        UserProfileAggregate userAggregate = userProfileAggregateService.getAggregateByUserId(userId);
        return buildResponse(userAggregate.user(), userAggregate.player().getDisplayName());
    }

    @Transactional
    public UserAdminResponse createUser(UserAdminCreateRequest request) {
        // TODO: Einmalpasswort generieren lassen
        // TODO: Einmalpasswort an den User via. Mail senden
        String tempOneTimePassword = "EinmalPasswort123!"; // FIXME: Einmalpasswort nicht setzen, sondern generieren
        User user = userService.createUserAsAdmin(request, tempOneTimePassword);
        PlayerProfile playerProfile = playerProfileService.createPlayerWithProfileAndStatistic(user.getId(),
                request.getDisplayName());
        userService.connectPlayer(user, playerProfile.getId());
        return buildResponse(user, playerProfile.getDisplayName());
    }

    @Transactional
    public UserAdminResponse patchUser(Long userId, UserAdminPatchRequest request) {
        User user = userService.patchUserAsAdmin(userId, request);
        PlayerProfile player = playerProfileService.editDisplayName(user.getPlayerId(), request.getDisplayName());

        return buildResponse(user, player.getDisplayName());
    }

    @NonNull
    private UserAdminResponse buildResponse(User user, String displayName) {
        return Objects.requireNonNull(UserAdminResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .customPermissions(user.getCustomPermissions())
                .lastPasswordChangeAt(user.getLastPasswordChangeAt())
                .status(user.getStatus())
                .activeBan(user.getActiveBan())
                .banHistory(user.getBanHistory())
                .displayName(displayName)
                .build());
    }
}
