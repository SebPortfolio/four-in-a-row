package de.paulm.four_in_a_row.service;

import java.util.Date;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.IllegalEmailException;
import de.paulm.four_in_a_row.domain.exceptions.IllegalUsernameException;
import de.paulm.four_in_a_row.domain.exceptions.PlayerProfileNotFoundException;
import de.paulm.four_in_a_row.player.PlayerProfile;
import de.paulm.four_in_a_row.repository.PlayerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {

    private final PlayerProfileRepository repository;

    public List<PlayerProfile> getAllPlayerProfiles() {
        return repository.findAll();
    }

    public List<PlayerProfile> getAllPlayerProfilesWithStatistic() {
        return repository.findAllWithStatistic();
    }

    public boolean doesPlayerProfileExsistById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }

        return repository.existsById(id);
    }

    @NonNull
    @SuppressWarnings("null")
    public PlayerProfile getPlayerProfileById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }
        return repository.findById(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id));
    }

    @NonNull
    @SuppressWarnings("null")
    public PlayerProfile getPlayerProfileByIdWithStatistic(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Profil-ID darf nicht null sein");
        }
        return repository.findByIdWithStatistic(id)
                .orElseThrow(() -> new PlayerProfileNotFoundException(id));
    }

    @Transactional
    public void editUsername(Long playerId, String newName) {
        PlayerProfile profile = this.getPlayerProfileById(playerId);
        if (newName == null || newName.isBlank()) {
            throw new IllegalUsernameException(newName, "Benutzername darf nicht leer sein");
        }
        if (newName.length() < 3) {
            throw new IllegalUsernameException(newName, "Benutzername muss mindestens 3 Zeichen haben");
        }
        profile.setUsername(newName);
    }

    @Transactional
    public void editEmail(Long playerId, String newEmail) {
        PlayerProfile profile = this.getPlayerProfileById(playerId);
        if (newEmail == null || !newEmail.matches(".+@.+\\..+")) {
            throw new IllegalEmailException(newEmail, "ungültiges Format");
        }
        profile.setEmail(newEmail);
    }

    @Transactional
    public PlayerProfile createPlayerProfile(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalUsernameException(username, "Benutzername darf nicht leer sein");
        }
        if (username.length() < 3) {
            throw new IllegalUsernameException(username, "Benutzername muss mindestens 3 Zeichen haben");
        }
        PlayerProfile profile = buildNewPlayerProfile(username, email);
        if (profile == null) {
            throw new RuntimeException("Fehler beim Erstellen des Spielerprofils");
        }

        return repository.save(profile);
    }

    private PlayerProfile buildNewPlayerProfile(String username, String email) {
        PlayerProfile profile = new PlayerProfile();
        profile.setUsername(username);
        profile.setEmail(email);
        profile.setRegisteredOn(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        return profile;
    }

    @Transactional
    public void deletePlayerProfile(Long playerId) {
        PlayerProfile profile = this.getPlayerProfileById(playerId);
        repository.delete(profile);
    }
}
