package de.paulm.four_in_a_row.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.paulm.four_in_a_row.game.Game;
import de.paulm.four_in_a_row.game.GameStatus;

public interface GameRepository extends JpaRepository<Game, Long> {
    public List<Game> findAllByStatusAndPlayer1OrPlayer2(GameStatus status, Long spieler1Id, Long spieler2Id);
}