package de.paulm.four_in_a_row.service;

import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.SpielerStatistikNotFoundException;
import de.paulm.four_in_a_row.profil.SpielerStatistik;
import de.paulm.four_in_a_row.repository.SpielerStatistikRespository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpielerStatistikService {

    private final SpielerStatistikRespository repository;

    public SpielerStatistik getSpielerStatistikById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SpielerStatistikNotFoundException(id));
    }

    @Transactional
    private void spielGewonnen(Long statistikId) {
        SpielerStatistik statistik = this.getSpielerStatistikById(statistikId);
        statistik.erhoeheNachSieg();
    }

    @Transactional
    private void spielVerloren(Long statistikId) {
        SpielerStatistik statistik = this.getSpielerStatistikById(statistikId);
        statistik.erhoeheNachNiederlage();
    }

}
