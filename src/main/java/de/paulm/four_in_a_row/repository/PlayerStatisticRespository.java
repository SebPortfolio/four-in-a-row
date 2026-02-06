package de.paulm.four_in_a_row.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.paulm.four_in_a_row.player.PlayerStatistic;

public interface PlayerStatisticRespository extends JpaRepository<PlayerStatistic, Long> {

    @Query("SELECT s FROM PlayerStatistic s LEFT JOIN FETCH s.playerProfile")
    List<PlayerStatistic> findAllWithProfile();

    @Query("SELECT s FROM PlayerStatistic s LEFT JOIN FETCH s.playerProfile WHERE s.id = :id")
    Optional<PlayerStatistic> findByIdWithProfile(@Param("id") Long id);
}
