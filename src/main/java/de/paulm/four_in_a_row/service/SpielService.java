package de.paulm.four_in_a_row.service;

import java.util.List;

import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.SpalteVollException;
import de.paulm.four_in_a_row.domain.exceptions.SpielNotFoundException;
import de.paulm.four_in_a_row.game.Spiel;
import de.paulm.four_in_a_row.game.SpielStatus;
import de.paulm.four_in_a_row.game.Spielfeld;
import de.paulm.four_in_a_row.profil.SpielerProfil;
import de.paulm.four_in_a_row.repository.SpielRepository;
import jakarta.transaction.Transactional;
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

    public Spiel ladeSpiel(Long spielId) {
        // TODO: Logik zum Laden aus einem Speicherort hinzufügen, Spielstatus ändern
        // etc.
        return this.getSpielById(spielId);
    }

    public Spiel speichereSpiel(Spiel spiel) {
        return repository.save(spiel);
    }

    public void loescheSpiel(Long spielId) {
        repository.deleteById(spielId);
    }

    @Transactional
    public void fuehreZugAus(Long spielId, byte spalte) {
        Spiel spiel = this.getSpielById(spielId);
        byte[][] board = spiel.getSpielfeld().getBoard();

        this.lasseSteinFallen(board, spalte, spiel.getAktuellerSpieler());
        spiel.getSpielfeld().setBoard(board);

        if (this.pruefeAufSieg(board, spiel.getAktuellerSpieler())) {
            spiel.setStatus(SpielStatus.ABGESCHLOSSEN);
        } else {
            this.naechsterSpieler(spiel);
        }
    }

    private void lasseSteinFallen(byte[][] board, byte spalte, byte aktuellerSpieler) {
        for (int reihe = board.length - 1; reihe >= 0; reihe--) {
            if (board[reihe][spalte] == 0) {
                board[reihe][spalte] = aktuellerSpieler;
                return;
            }
        }
        throw new SpalteVollException(spalte);
    }

    private boolean pruefeAufSieg(byte[][] board, byte aktuellerSpielerId) {
        int rows = board.length;
        int cols = board[0].length;

        for (int reihe = rows - 1; reihe >= 0; reihe--) {
            for (int spalte = 0; spalte < cols; spalte++) {
                if (board[reihe][spalte] != aktuellerSpielerId) {
                    continue;
                }

                // Horizontal nach rechts
                if (spalte + 3 < cols &&
                        board[reihe][spalte + 1] == aktuellerSpielerId &&
                        board[reihe][spalte + 2] == aktuellerSpielerId &&
                        board[reihe][spalte + 3] == aktuellerSpielerId) {
                    return true;
                }

                // Vertikal nach oben
                if (reihe - 3 >= 0 &&
                        board[reihe - 1][spalte] == aktuellerSpielerId &&
                        board[reihe - 2][spalte] == aktuellerSpielerId &&
                        board[reihe - 3][spalte] == aktuellerSpielerId) {
                    return true;
                }

                // Diagonal nach rechts oben
                if (reihe - 3 >= 0 && spalte + 3 < cols &&
                        board[reihe - 1][spalte + 1] == aktuellerSpielerId &&
                        board[reihe - 2][spalte + 2] == aktuellerSpielerId &&
                        board[reihe - 3][spalte + 3] == aktuellerSpielerId) {
                    return true;
                }

                // Diagonal nach links oben
                if (reihe - 3 >= 0 && spalte - 3 >= 0 &&
                        board[reihe - 1][spalte - 1] == aktuellerSpielerId &&
                        board[reihe - 2][spalte - 2] == aktuellerSpielerId &&
                        board[reihe - 3][spalte - 3] == aktuellerSpielerId) {
                    return true;
                }
            }
        }
        return false;
    }

    private void naechsterSpieler(Spiel spiel) {
        byte aktuellerSpieler = spiel.getAktuellerSpieler();
        spiel.setAktuellerSpieler(aktuellerSpieler == 1 ? (byte) 2 : (byte) 1);
    }
}
