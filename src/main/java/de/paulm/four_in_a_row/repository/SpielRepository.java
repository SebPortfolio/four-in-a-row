package de.paulm.four_in_a_row.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.paulm.four_in_a_row.game.Spiel;
import de.paulm.four_in_a_row.game.SpielStatus;

public interface SpielRepository extends JpaRepository<Spiel, Long> {
    public List<Spiel> findAllByStatusAndSpieler1OrSpieler2(SpielStatus status, Long spieler1Id, Long spieler2Id);
}