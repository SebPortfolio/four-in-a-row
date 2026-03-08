package de.paulm.four_in_a_row.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import de.paulm.four_in_a_row.domain.exceptions.BanNotActiveException;
import de.paulm.four_in_a_row.domain.exceptions.BanUserMismatchException;
import de.paulm.four_in_a_row.domain.exceptions.IllegalDateRangeException;
import de.paulm.four_in_a_row.domain.exceptions.OtherBanActiveException;
import de.paulm.four_in_a_row.domain.security.Ban;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.web.dtos.ban.BanCreateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.BanPermanentCreateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.BanUpdateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.CancelBanRequest;

@Component
public class BanRequestValidationService {

    public void validateCreate(BanCreateRequest request, User user) {
        validateUserHasNoActiveBan(user);
        validateEndAfterStart(LocalDateTime.now(), request.getEndAt());
    }

    public void validateCreatePerma(BanPermanentCreateRequest request, User user) {
        validateUserHasNoActiveBan(user);
    }

    public void validateUpdate(Long userId, Ban currentBan, BanUpdateRequest request) {
        validateUserHasThisBan(currentBan, userId);
        validateBanIsActive(currentBan);
        validateEndAfterStart(currentBan.getStartAt(), currentBan.getEndAt());
    }

    public void validateCancel(Long userId, Ban currentBan, CancelBanRequest request) {
        validateUserHasThisBan(currentBan, userId);
        validateBanIsActive(currentBan);
    }

    private void validateUserHasNoActiveBan(User user) {
        if (user.getActiveBan() != null) {
            throw new OtherBanActiveException(user.getActiveBan().getReason());
        }
    }

    private void validateUserHasThisBan(Ban ban, Long userId) {
        if (!ban.getUser().getId().equals(userId)) {
            throw new BanUserMismatchException(ban.getId(), userId);
        }
    }

    private void validateBanIsActive(Ban ban) {
        if (!ban.isActive()) {
            throw new BanNotActiveException();
        }
    }

    private void validateEndAfterStart(LocalDateTime startAt, LocalDateTime endAt) {
        if (endAt == null) {
            return;
        }
        if (endAt.isBefore(startAt) || endAt.isEqual(startAt)) {
            throw new IllegalDateRangeException(startAt, endAt);
        }
    }
}
