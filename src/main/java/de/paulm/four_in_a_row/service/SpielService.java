package de.paulm.four_in_a_row.service;

import java.util.List;

import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.SpielNotFoundException;
import de.paulm.four_in_a_row.game.Spiel;
import de.paulm.four_in_a_row.game.SpielStatus;
import de.paulm.four_in_a_row.profil.SpielerProfil;
import de.paulm.four_in_a_row.repository.SpielRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpielService {

    private final SpielRepository repository;

    public Spiel getSpielById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SpielNotFoundException(id));
    }

    public List<Spiel> getPausierteSpieleFuerSpieler(Long spielerId) {
        return repository.findAllByStatusAndSpieler1OrSpieler2(SpielStatus.PAUSIERT, spielerId, spielerId);
    }

    public Spiel erstelleNeuesSpiel(SpielerProfil spieler1, SpielerProfil spieler2) {
        Spiel spiel = new Spiel(spieler1, spieler2);
        return repository.save(spiel);
    }
}
