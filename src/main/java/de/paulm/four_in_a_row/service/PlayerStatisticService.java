package de.paulm.four_in_a_row.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.PlayerStatisticNotFoundException;
import de.paulm.four_in_a_row.player.PlayerProfile;
import de.paulm.four_in_a_row.player.PlayerStatistic;
import de.paulm.four_in_a_row.repository.PlayerStatisticRespository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerStatisticService {

    private final PlayerStatisticRespository repository;

    public PlayerStatistic getStatisticById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Spieler-Statistik-ID darf nicht null sein");
        }
        return repository.findById(id)
                .orElseThrow(() -> new PlayerStatisticNotFoundException(id));
    }

    @Transactional
    public void gameWon(PlayerProfile playerProfile) {
        PlayerStatistic statistic = playerProfile.getStatistic();
        this.updateStatsticAfterVictory(statistic);
    }

    @Transactional
    public void gameLost(PlayerProfile playerProfile, boolean isSurrender) {
        PlayerStatistic statistic = playerProfile.getStatistic();
        this.updateStatsticAfterLose(statistic, isSurrender);
    }

    @Transactional
    public void gameDrawn(PlayerProfile playerProfile) {
        PlayerStatistic statistic = playerProfile.getStatistic();
        statistic.setTotalGames(statistic.getTotalGames() + 1);
        statistic.setLastPlayedOn(LocalDate.now());
    }

    private void updateStatsticAfterVictory(PlayerStatistic statistic) {
        statistic.setTotalGames(statistic.getTotalGames() + 1);
        statistic.setGamesWon(statistic.getGamesWon() + 1);
        statistic.setLastPlayedOn(LocalDate.now());
    }

    private void updateStatsticAfterLose(PlayerStatistic statistic, boolean isSurrender) {
        statistic.setTotalGames(statistic.getTotalGames() + 1);
        statistic.setGamesLost(statistic.getGamesLost() + 1);
        statistic.setGamesSurrendered(statistic.getGamesSurrendered() + 1);
        statistic.setLastPlayedOn(LocalDate.now());
    }
}
