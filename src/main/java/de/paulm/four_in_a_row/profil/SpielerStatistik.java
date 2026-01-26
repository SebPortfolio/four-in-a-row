package de.paulm.four_in_a_row.profil;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class SpielerStatistik {

    @Id
    @GeneratedValue
    private Long id;

    private Long spieleGesamt;

    private Long spieleGewonnen;

    private LocalDate zuletztGespieltAm;

    public void erhoeheNachSieg() {
        this.spieleGesamt = this.spieleGesamt + 1;
        this.spieleGewonnen = this.spieleGewonnen + 1;
        this.zuletztGespieltAm = LocalDate.now();
    }

    public void erhoeheNachNiederlage() {
        this.spieleGesamt = this.spieleGesamt + 1;
        this.zuletztGespieltAm = LocalDate.now();
    }
}
