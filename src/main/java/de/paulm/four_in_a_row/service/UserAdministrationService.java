package de.paulm.four_in_a_row.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.PlayerProfileNotFoundException;
import de.paulm.four_in_a_row.domain.exceptions.UserNotFoundException;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserProjection;
import de.paulm.four_in_a_row.repository.UserRepository;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminMasterDataResponse;
import de.paulm.four_in_a_row.web.dtos.UserAdminOverviewResponse;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdministrationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PlayerProfileService playerProfileService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<UserAdminResponse> getUsersAsAdmin() {
        List<UserProjection> projections = userRepository.findAllProjections();
        return projections.stream()
                .map(projection -> buildUserAdminResponse(projection))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserAdminResponse getUserByIdAsAdmin(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId darf nicht null sein");
        }
        UserProjection projection = userRepository.findProjectionById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        log.debug("porjection.user.lastModifiedAt: {}", projection.user().getLastModifiedAt());
        return buildUserAdminResponse(projection);
    }

    @Transactional
    public UserAdminResponse createUser(UserAdminCreateRequest request) {
        // TODO: Einmalpasswort generieren lassen
        // TODO: Einmalpasswort an den User via. Mail senden
        String tempOneTimePassword = "EinmalPasswort123!"; // FIXME: Einmalpasswort nicht setzen, sondern generieren

        User user = userService.createUserAsAdmin(request, tempOneTimePassword);
        PlayerProfile player = playerProfileService.createPlayerWithProfileAndStatistic(user.getId(),
                request.getDisplayName());
        userService.connectPlayer(user, player.getId());

        return buildUserAdminResponse(new UserProjection(user, player.getDisplayName()));
    }

    @Transactional
    public UserAdminResponse patchUser(Long userId, UserAdminPatchRequest request) {
        User user = userService.patchUserAsAdmin(userId, request);
        PlayerProfile player = playerProfileService.getProfileById(user.getPlayerId());

        return buildUserAdminResponse(new UserProjection(user, player.getDisplayName()));
    }

    @Transactional
    public String getClearTextEmail(Long targetUserId) {
        User targetUser = userService.getUserById(targetUserId);

        auditService.logRevealEmailByAdmin(targetUserId);

        return targetUser.getEmail();
    }

    @Transactional(readOnly = true)
    public List<UserAdminOverviewResponse> getUsersForAdminOverview() {
        List<UserProjection> projections = userRepository.findAllProjections();
        return projections.stream()
                .map(projection -> buildUserAdminOverviewResponse(projection))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserAdminMasterDataResponse getUserMasterData(Long userId) {
        UserProjection projection = userRepository.findProjectionById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return buildUserAdminMasterDataResponse(projection);
    }

    private UserAdminResponse buildUserAdminResponse(UserProjection projection) {
        User user = projection.user();
        return UserAdminResponse.builder()
                .id(user.getId())
                .displayName(projection.displayName())
                .email(userService.maskEmail(user.getEmail()))
                .roles(user.getRoles())
                .customPermissions(user.getCustomPermissions())
                .lastPasswordChangeAt(user.getLastPasswordChangeAt())
                .status(user.getStatus())
                .activeBan(user.getActiveBan())
                .bans(user.getBans())
                .playerId(user.getPlayerId())
                .lastModifiedAt(user.getLastModifiedAt())
                .lastModifiedByUserId(user.getLastModifiedByUserId())
                .build();
    }

    private UserAdminOverviewResponse buildUserAdminOverviewResponse(UserProjection projection) {
        User user = projection.user();
        return UserAdminOverviewResponse.builder()
                .id(user.getId())
                .displayName(projection.displayName())
                .maskedEmail(userService.maskEmail(user.getEmail()))
                .status(user.getStatus())
                .roles(user.getRoles())
                .hasCustomPermissions(user.getCustomPermissions() != null && !user.getCustomPermissions().isEmpty())
                .banned(user.getActiveBan() != null)
                .build();
    }

    private UserAdminMasterDataResponse buildUserAdminMasterDataResponse(UserProjection projection) {
        User user = projection.user();
        String lastModifiedDisplayName = null;
        if (user.getLastModifiedAt() != null) {
            try {
                lastModifiedDisplayName = playerProfileService.getProfileByUserId(user.getLastModifiedByUserId())
                        .getDisplayName();
            } catch (PlayerProfileNotFoundException e) {
                log.info("letzter Bearbeiter #{} ist nicht als Player gespeichert", user.getLastModifiedByUserId());
                lastModifiedDisplayName = "Deleted_Player_" + user.getLastModifiedByUserId();
            }

        }
        return UserAdminMasterDataResponse.builder()
                .id(user.getId())
                .displayName(projection.displayName())
                .maskedEmail(userService.maskEmail(user.getEmail()))
                .status(user.getStatus())
                .roles(user.getRoles())
                .customPermissions(user.getCustomPermissions())
                .lastModifiedAt(user.getLastModifiedAt())
                .lastModifiedByUserId(user.getLastModifiedByUserId())
                .lastModifiedByDisplayName(lastModifiedDisplayName)
                .build();
    }

}
