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
public class SpielerProfil {

    @Id
    @GeneratedValue
    private Long id;

    private String benutzername;

    private String email;

    private LocalDate registriertAm;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private SpielerStatistik statistik;

    public void benutzernameAendern(String neuerName) {
        if (neuerName == null || neuerName.isBlank()) {
            throw new IllegalArgumentException("Benutzername darf nicht leer sein");
        }
        if (neuerName.length() < 3) {
            throw new IllegalArgumentException("Benutzername muss mindestens 3 Zeichen haben");
        }
        this.benutzername = neuerName;
    }

    public void emailAendern(String neueEmail) {
        if (neueEmail == null || !neueEmail.matches(".+@.+\\..+")) {
            throw new IllegalArgumentException("Ungültige Email");
        }
        this.email = neueEmail;
    }
}
