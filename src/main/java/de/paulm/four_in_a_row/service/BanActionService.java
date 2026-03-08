package de.paulm.four_in_a_row.service;

import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.security.Ban;
import de.paulm.four_in_a_row.domain.security.BanAction;
import de.paulm.four_in_a_row.repository.BanActionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BanActionService {

    private final BanActionRepository repository;

    @Transactional
    public BanAction logAction(Ban ban, Long executingUserId, String oldValue, String comment) {
        BanAction action = buildBanAction(ban, executingUserId, oldValue, ban.toSnapshot(), comment);

        return repository.save(action);
    }

    @NonNull
    private BanAction buildBanAction(
            Ban ban,
            Long executingUserId,
            String oldValue,
            String newValue,
            String comment) {
        return Objects.requireNonNull(BanAction.builder()
                .ban(ban)
                .executingUserId(executingUserId)
                .oldValue(oldValue)
                .newValue(newValue)
                .comment(comment)
                .build());
    }

}
