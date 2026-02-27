package de.paulm.four_in_a_row.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.RegistrationException;
import de.paulm.four_in_a_row.domain.player.PlayerProfile;
import de.paulm.four_in_a_row.domain.security.AuthUserResponse;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.four_in_a_row.domain.security.UserSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final UserSessionService userSessionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PlayerProfileService playerProfileService;

    @Transactional
    public AuthUserResponse register(String email, String password, String displayName, String ipAdressStr,
            String userAgent) {
        if (userService.existsByEmail(email)) {
            // TODO: Bestätigungsmail an bestehenden User,
            // ob er sich neu registrieren wollte?
            throw new RegistrationException("Ein Account mit dieser Email ist bereits vergeben!");
        }

        if (!displayName.matches("^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){1,28}[a-zA-Z0-9]$")) {
            throw new RegistrationException("DisplayName entspricht nicht den Richtlinien.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = userService.buildNewUser(email, encodedPassword);
        user = userService.saveUser(user);
        PlayerProfile playerProfile = playerProfileService.createProfileForUser(user.getId(), displayName);

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
        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken;

        if (oldRefreshToken == null) {
            // Ganz neuer Login ohne altes Cookie
            newRefreshToken = userSessionService.createSession(user.getId(), ipAdressStr, userAgent).getRefreshToken();
        } else {
            try {
                newRefreshToken = userSessionService.renewRefreshToken(oldRefreshToken, ipAdressStr, userAgent);
            } catch (Exception e) {
                // Token war da, aber ungültig -> Neue Session
                newRefreshToken = userSessionService.createSession(user.getId(), ipAdressStr, userAgent)
                        .getRefreshToken();
            }
        }

        UserProfileAggregate userContextResponse = buildUserProfileAggregate(user);
        return new AuthUserResponse(accessToken, newRefreshToken, userContextResponse);
    }

    public void changePassword(String oldPassword, String newPassword, String currentRefreshToken) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new BadCredentialsException("Altes Passwort ist nicht korrekt.");
        }

        userService.changePassword(currentUser.getId(), passwordEncoder.encode(newPassword));
        userSessionService.logoutEverywhereButCurrent(currentUser.getId(), currentRefreshToken);
    }

    public AuthUserResponse refreshSession(String oldRefreshToken) {
        UserSession oldSession = userSessionService.getSessionByRefreshToken(oldRefreshToken);
        User user = userService.getUserByIdWithRolesAndPermissions(oldSession.getUserId());

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