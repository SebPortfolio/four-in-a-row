package de.paulm.four_in_a_row.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import de.paulm.four_in_a_row.profil.SpielerStatistik;

public interface SpielerStatistikRespository extends JpaRepository<SpielerStatistik, Long> {
}
