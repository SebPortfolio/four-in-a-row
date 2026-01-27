package de.paulm.four_in_a_row.profil;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PlayerProfile {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String email;

    private LocalDate registeredOn;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PlayerStatistic statistic;

    public void changeUsername(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Benutzername darf nicht leer sein");
        }
        if (newName.length() < 3) {
            throw new IllegalArgumentException("Benutzername muss mindestens 3 Zeichen haben");
        }
        this.username = newName;
    }

    public void changeEmail(String newEmail) {
        if (newEmail == null || !newEmail.matches(".+@.+\\..+")) {
            throw new IllegalArgumentException("Ungültige Email");
        }
        this.email = newEmail;
    }
}
