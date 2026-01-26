package de.paulm.four_in_a_row.game;

import de.paulm.four_in_a_row.profil.SpielerProfil;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Spiel {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private SpielerProfil spieler1;

    @OneToOne
    private SpielerProfil spieler2;

    @Enumerated(EnumType.STRING)
    private SpielStatus status;

    private Long aktuellerSpielerId;

    public Spiel(SpielerProfil spieler1, SpielerProfil spieler2) {
        this.spieler1 = spieler1;
        this.spieler2 = spieler2;
        this.status = SpielStatus.IN_PROGRESS;
    }
}
