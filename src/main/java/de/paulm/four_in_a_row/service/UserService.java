package de.paulm.four_in_a_row.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.IllegalEmailException;
import de.paulm.four_in_a_row.domain.exceptions.UserNotFoundException;
import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserStatus;
import de.paulm.four_in_a_row.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BanService banService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User getUserById(Long id) throws UserNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("id darf nicht null sein");
        }
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException());

        return banService.checkAndHandleExpiredBan(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Es wird nach einem identifier gesucht.
        // Dieser kann sowohl Email, als auch Username sein.
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Benutzer nicht gefunden: " + email));
        return banService.checkAndHandleExpiredBan(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User buildNewUser(String email, String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .authorities(Set.of(Role.ROLE_USER))
                .status(UserStatus.UNVERIFIED)
                .build();
    }

    public User saveUser(User user) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("user darf nicht null sein");
        }
        return userRepository.save(user);
    }

    @Transactional
    public void editEmail(Long playerId, String newEmail) throws IllegalArgumentException {
        User user = this.getUserById(playerId);
        if (newEmail == null || !newEmail.matches(".+@.+\\..+")) {
            throw new IllegalEmailException(newEmail, "ungültiges Format");
        }
        user.setEmail(newEmail);
    }

    @Transactional
    public void changePassword(Long userId, String newRawPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newRawPassword));
        user.setLastPasswordChangeAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteAndAnonymizeUser(Long userId) {
        User user = getUserById(userId);

        log.info("Anonymisiere User ID: {} aufgrund von Löschanfrage.", userId);

        user.setStatus(UserStatus.DELETED);

        // Anonymisierung
        // TODO: anzeigename annonymisieren
        user.setEmail("deleted_" + userId + "@internal.fourinarow.de");
        user.setPassword("ANONYMIZED_" + LocalDateTime.now());

        user.setBannedUntil(null);
        user.getAuthorities().clear();
    }
}
