package de.paulm.four_in_a_row.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import de.paulm.four_in_a_row.profil.PlayerProfile;

public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Long> {
}
