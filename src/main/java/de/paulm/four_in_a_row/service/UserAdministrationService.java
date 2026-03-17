package de.paulm.four_in_a_row.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.UserNotFoundException;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserProjection;
import de.paulm.four_in_a_row.repository.UserRepository;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdministrationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PlayerProfileService playerProfileService;

    @Transactional(readOnly = true)
    public List<UserProjection> getUsersAsAdmin() {
        return userRepository.findAllProjections();
    }

    @Transactional(readOnly = true)
    public UserProjection getUserByIdAsAdmin(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId darf nicht null sein");
        }
        return userRepository.findProjectionById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    public UserProjection createUser(UserAdminCreateRequest request) {
        // TODO: Einmalpasswort generieren lassen
        // TODO: Einmalpasswort an den User via. Mail senden
        String tempOneTimePassword = "EinmalPasswort123!"; // FIXME: Einmalpasswort nicht setzen, sondern generieren

        User user = userService.createUserAsAdmin(request, tempOneTimePassword);
        PlayerProfile player = playerProfileService.createPlayerWithProfileAndStatistic(user.getId(),
                request.getDisplayName());
        userService.connectPlayer(user, player.getId());

        return new UserProjection(user, player.getDisplayName());
    }

    @Transactional
    public UserProjection patchUser(Long userId, UserAdminPatchRequest request) {
        User user = userService.patchUserAsAdmin(userId, request);
        PlayerProfile player = playerProfileService.getProfileById(user.getPlayerId());

        return new UserProjection(user, player.getDisplayName());
    }
}
