package de.paulm.four_in_a_row.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.UserNotFoundException;
import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserStatus;
import de.paulm.four_in_a_row.repository.UserRepository;
import de.paulm.four_in_a_row.web.dtos.RegisterRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserValidationService validationService;
    private final PasswordEncoder passwordEncoder;

    private static final Set<Role> DEFAULT_ROLES = Set.of(Role.ROLE_USER);
    private static final Set<Permission> DEFAULT_PERMISSIONS = Set.of();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return internalFindById(id);
    }

    private User internalFindById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException());

        return user;
    }

    // Businessprozess
    @Transactional
    public User getUserByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("email darf nicht null sein");
        }
        User user = userRepository.findByEmailEager(email).orElseThrow(
                () -> new UserNotFoundException());

        return user;
    }

    // Authentifizierungsprozess
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        if (email == null) {
            throw new IllegalArgumentException("email darf nicht null sein");
        }
        try {
            return userRepository.findByEmailWithAuthorities(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Username nicht gefunden: " + email));
        } catch (UserNotFoundException ex) {
            throw new UsernameNotFoundException("User nicht gefunden", ex);
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User createUserFromRegister(RegisterRequest request) {
        return validateAndCreateUser(request.getEmail(), request.getPassword(), DEFAULT_ROLES, DEFAULT_PERMISSIONS);
    }

    @Transactional
    public User createUserAsAdmin(UserAdminCreateRequest request, String rawOneTimePassword) {
        return validateAndCreateUser(request.getEmail(), rawOneTimePassword, request.getRoles(),
                request.getCustomPermissions());
    }

    private User validateAndCreateUser(String email, String rawPassword, Set<Role> roles, Set<Permission> permissions) {
        validationService.validateEmail(email, null);
        validationService.validatePassword(rawPassword);

        if (roles == null) {
            roles = DEFAULT_ROLES;
        }
        if (permissions == null) {
            permissions = DEFAULT_PERMISSIONS;
        }

        User user = buildNewUser(email, encodePassword(rawPassword), roles, permissions);
        return userRepository.save(user);
    }

    @Transactional
    public User editEmail(Long userId, String newEmail) {
        User user = internalFindById(userId);
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            applyEmailChange(user, newEmail);
        }
        return user;
    }

    @Transactional
    public User patchUserAsAdmin(Long userId, UserAdminPatchRequest request) {
        User user = internalFindById(userId);

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            applyEmailChange(user, request.getEmail());
        }

        if (request.getRoles() != null && !request.getRoles().equals(user.getRoles())) {
            applyRolesChange(user, request.getRoles());
        }

        if (request.getCustomPermissions() != null
                && !request.getCustomPermissions().equals(user.getCustomPermissions())) {
            applyCustomPermissionsChange(user, request.getCustomPermissions());
        }

        return user;
    }

    private void applyEmailChange(User user, String newEmail) {
        validationService.validateEmail(newEmail, user.getId());
        user.setEmail(newEmail);
        // TODO: Verifizierung zurücksetzen, sobald es überhaupt eine gibt
        log.debug("Email für User #{} geändert zu: '{}', Verifizierung zurückgesetzt",
                user.getId(), newEmail);
    }

    private void applyRolesChange(User user, Set<Role> roles) {
        user.updateRoles(roles);
        log.debug("Rollen für User #{} geändert zu: {}",
                user.getId(), roles.toString());
    }

    private void applyCustomPermissionsChange(User user, Set<Permission> customPermissions) {
        user.updateCustomPermissions(customPermissions);
        log.debug("custom Berechtigungen für User #{} geändert zu: {}",
                user.getId(), customPermissions.toString());
    }

    @Transactional
    public void changePassword(Long userId, String rawPassword) {
        User user = internalFindById(userId);

        validationService.validatePassword(rawPassword);

        user.setPassword(encodePassword(rawPassword));
        user.setLastPasswordChangeAt(LocalDateTime.now());
        log.debug("password geändert and lastPasswordChange aktualisiert");
    }

    @Transactional
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Transactional
    public void deleteUserById(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void connectPlayer(User user, Long playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId darf nicht null sein");
        }
        if (playerCanBeConnected(user)) {
            user.setPlayerId(playerId);
        } else {
            log.warn("User #{} kann nicht mit Player #{} verbunden werden", user.getId(), playerId);
        }
    }

    private boolean playerCanBeConnected(User user) {
        return user.getPlayerId() == null && user.getStatus() == UserStatus.ACTIVE;
    }

    @NonNull
    private User buildNewUser(String email, String encodedPassword, Set<Role> roles,
            Set<Permission> customPermissions) {
        return Objects.requireNonNull(User.builder()
                .email(email)
                .password(encodedPassword)
                .roles(roles)
                .customPermissions(customPermissions)
                .status(UserStatus.ACTIVE) // TODO: auf UNVERIFIED setzen & Email Verifikation mit Einmalcode
                .build());
    }

}
