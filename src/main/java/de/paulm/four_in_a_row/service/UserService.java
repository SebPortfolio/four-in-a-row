package de.paulm.four_in_a_row.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.IllegalEmailException;
import de.paulm.four_in_a_row.domain.exceptions.UserNotFoundException;
import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserStatus;
import de.paulm.four_in_a_row.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BanService banService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User getUserById(Long id) throws UserNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException());

        return banService.checkAndHandleExpiredBan(user);
    }

    @Transactional
    public User getUserByIdWithRolesAndPermissions(Long id) throws UserNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }
        User user = userRepository.findByIdWithRolesAndPermissions(id).orElseThrow(
                () -> new UserNotFoundException());

        return banService.checkAndHandleExpiredBan(user);
    }

    // Businessprozess
    @Transactional
    public User getUserByEmail(String email) throws UserNotFoundException, IllegalArgumentException {
        if (email == null) {
            throw new IllegalArgumentException("email darf nicht null sein");
        }
        User user = userRepository.findByEmailWithRolesAndPermissions(email).orElseThrow(
                () -> new UserNotFoundException());
        return banService.checkAndHandleExpiredBan(user);
    }

    // Authentifizierungsprozess
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return getUserByEmail(email);
        } catch (UserNotFoundException ex) {
            throw new UsernameNotFoundException("Username nicht gefunden", ex);
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @NonNull
    public User buildNewUser(String email, String encodedPassword) {
        return Objects.requireNonNull(User.builder()
                .email(email)
                .password(encodedPassword)
                .roles(Set.of(Role.ROLE_USER))
                .status(UserStatus.ACTIVE) // TODO: auf UNVERIFIED setzen & Email Verifikation mit Einmalcode
                .build());
    }

    public User saveUser(@NonNull User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void editEmail(User user, String newEmail) throws IllegalEmailException {
        validateEmail(newEmail);
        user.setEmail(newEmail);
    }

    @Transactional
    public void changePassword(Long userId, String newEncodedPassword) {
        User user = getUserById(userId);
        user.setPassword(newEncodedPassword);
        user.setLastPasswordChangeAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteUserById(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    private void validateEmail(String email) throws IllegalEmailException {
        if (email == null || email.isBlank()) {
            throw new IllegalEmailException(email, "null oder blank");
        }
        if (email.length() > User.EMAIL_MAX_LENGTH) {
            throw new IllegalEmailException(email, "zu lang, maximal " + User.EMAIL_MAX_LENGTH + " Zeichen");
        }
    }
}
