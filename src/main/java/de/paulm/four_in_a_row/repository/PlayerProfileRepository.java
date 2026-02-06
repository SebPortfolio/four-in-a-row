package de.paulm.four_in_a_row.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.paulm.four_in_a_row.player.PlayerProfile;

public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Long> {

    @Query("SELECT p FROM PlayerProfile p LEFT JOIN FETCH p.playerStatistic")
    List<PlayerProfile> findAllWithStatistic();

    @Query("SELECT p FROM PlayerProfile p LEFT JOIN FETCH p.playerStatistic WHERE p.id = :id")
    Optional<PlayerProfile> findByIdWithStatistic(@Param("id") Long id);
}
