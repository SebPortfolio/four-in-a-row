package de.paulm.four_in_a_row.service;

import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.RegistrationException;
import de.paulm.four_in_a_row.domain.exceptions.UserSessionNotFoundException;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.domain.security.AuthUserResponse;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.four_in_a_row.domain.security.UserSession;
import de.paulm.four_in_a_row.web.dtos.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserService userService;
    private final UserSessionService userSessionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PlayerProfileService playerProfileService;

    @Transactional
    public AuthUserResponse register(RegisterRequest request, String ipAdressStr, String userAgent) {
        if (userService.existsByEmail(request.getEmail())) {
            // TODO: Bestätigungsmail an bestehenden User,
            // ob er sich neu registrieren wollte?
            throw new RegistrationException(
                    "Die Registrierung konnte nicht abgeschlossen werden. Bitte prüfen Sie Ihre Eingaben");
        }

        User user = userService.createUserFromRegister(request);
        PlayerProfile playerProfile = playerProfileService.createPlayerWithProfileAndStatistic(user.getId(),
                request.getDisplayName());
        userService.connectPlayer(user, playerProfile.getId());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = userSessionService.createSession(user.getId(), ipAdressStr, userAgent).getRefreshToken();

        UserProfileAggregate userProfileAgg = new UserProfileAggregate(user, playerProfile);
        return new AuthUserResponse(accessToken, refreshToken, userProfileAgg);
    }

    public AuthUserResponse login(String email, String password, String ipAdressStr, String userAgent,
            String oldRefreshToken) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        User user = userService.getUserByEmail(email);
        final Long userId = Objects.requireNonNull(user.getId(),
                "userId darf nach authentifizieren nicht null sein");
        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = null;

        if (oldRefreshToken != null) {
            try {
                newRefreshToken = userSessionService.renewRefreshToken(oldRefreshToken, ipAdressStr, userAgent);
            } catch (UserSessionNotFoundException e) {
                // altes Token nach Logout noch mitgesandt oder
                // Angreifer mit altem/erfundenem Token
                log.warn("Login mit ungültigem RefreshToken für User #{}, bereinige Sessions", userId);
                userSessionService.deleteAllSessionsByUserId(userId);
            }
        }

        if (oldRefreshToken == null || newRefreshToken == null) {
            newRefreshToken = userSessionService.createSession(userId, ipAdressStr, userAgent).getRefreshToken();
        }

        UserProfileAggregate userContextResponse = buildUserProfileAggregate(user);
        return new AuthUserResponse(accessToken, newRefreshToken, userContextResponse);
    }

    public void changePassword(String oldPassword, String newPassword, String currentRefreshToken) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new BadCredentialsException("Altes Passwort ist nicht korrekt.");
        }

        userService.changePassword(currentUser.getId(), newPassword);
        userSessionService.logoutEverywhereButCurrent(currentUser.getId(), currentRefreshToken);
    }

    public AuthUserResponse refreshSession(String oldRefreshToken) {
        UserSession oldSession = userSessionService.getSessionByRefreshToken(oldRefreshToken);
        User user = userService.getUserById(oldSession.getUserId());

        String refreshToken = userSessionService.renewRefreshToken(oldRefreshToken, null, null);
        String accessToken = jwtService.generateAccessToken(user);

        UserProfileAggregate userProfileAgg = buildUserProfileAggregate(user);
        return new AuthUserResponse(accessToken, refreshToken, userProfileAgg);
    }

    private UserProfileAggregate buildUserProfileAggregate(User user) {
        PlayerProfile profile = playerProfileService.getProfileByUserId(user.getId());
        return new UserProfileAggregate(user, profile);
    }
}