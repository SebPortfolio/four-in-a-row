package de.paulm.four_in_a_row.service;

import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.PlayerStatisticNotFoundException;
import de.paulm.four_in_a_row.profil.PlayerStatistic;
import de.paulm.four_in_a_row.repository.PlayerStatisticRespository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerStatisticService {

    private final PlayerStatisticRespository repository;

    public PlayerStatistic getSpielerStatistikById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PlayerStatisticNotFoundException(id));
    }

    @Transactional
    private void gameWon(Long statisticId) {
        PlayerStatistic statistic = this.getSpielerStatistikById(statisticId);
        statistic.raiseCountAfterVictory();
    }

    @Transactional
    private void gameLost(Long statisticId) {
        PlayerStatistic statistic = this.getSpielerStatistikById(statisticId);
        statistic.raiseCountAfterLose();
    }

}
