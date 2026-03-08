package de.paulm.four_in_a_row.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.exceptions.BanNotFoundException;
import de.paulm.four_in_a_row.domain.exceptions.UserNotFoundException;
import de.paulm.four_in_a_row.domain.security.Ban;
import de.paulm.four_in_a_row.domain.security.BanAction;
import de.paulm.four_in_a_row.domain.security.BanReason;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.repository.BanRepository;
import de.paulm.four_in_a_row.repository.UserRepository;
import de.paulm.four_in_a_row.web.dtos.ban.BanCreateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.BanPermanentCreateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.BanUpdateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.CancelBanRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BanService {

    private final BanRepository banRepository;
    private final UserRepository userRepository;
    private final BanActionService banActionService;
    private final BanRequestValidationService banRequestValidationService;

    @Transactional(readOnly = true)
    public Ban getBanById(Long banId) {
        if (banId == null) {
            throw new IllegalArgumentException("banId darf nicht null sein");
        }

        return banRepository.findById(banId).orElseThrow(() -> new BanNotFoundException(banId));
    }

    @Transactional
    public Ban createBan(Long userId, BanCreateRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("userId darf nicht null sein");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        banRequestValidationService.validateCreate(request, user);

        Ban ban = buildBan(user,
                request.getEndAt(),
                request.getReason(),
                request.getInternalNote(),
                request.getExecutingUserId());

        BanAction createAction = banActionService.logAction(
                ban,
                request.getExecutingUserId(),
                null,
                null);

        ban.addAction(createAction);
        user.addBan(ban);

        log.info("Bann erstellt: {}", ban);
        return banRepository.save(ban);
    }

    @Transactional
    public Ban createPermaBan(Long userId, BanPermanentCreateRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("userId darf nicht null sein");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        banRequestValidationService.validateCreatePerma(request, user);

        Ban ban = buildBan(user,
                null,
                request.getReason(),
                request.getInternalNote(),
                request.getExecutingUserId());

        BanAction createAction = banActionService.logAction(
                ban,
                request.getExecutingUserId(),
                null,
                null);

        ban.addAction(createAction);
        user.addBan(ban);

        log.info("Perma-Bann erstellt: {}", ban);
        return banRepository.save(ban);
    }

    @Transactional
    public Ban updateBan(Long userId, Long banId, BanUpdateRequest request) {
        Ban ban = getBanById(banId);
        banRequestValidationService.validateUpdate(userId, ban, request);

        String oldValue = ban.toSnapshot();

        ban.setReason(request.getReason());
        ban.setInternalNote(request.getInternalNote());
        ban.setEndAt(request.getNewEndAt());

        BanAction editAction = banActionService.logAction(ban,
                request.getExecutingUserId(),
                oldValue,
                request.getComment());

        ban.addAction(editAction);

        log.info("Bann {} editiert: {}", banId, editAction);
        return ban;
    }

    @Transactional
    public Ban cancelBan(Long userId, Long banId, CancelBanRequest request) {
        Ban ban = getBanById(banId);
        banRequestValidationService.validateCancel(userId, ban, request);

        String oldValue = ban.toSnapshot();

        ban.setCancelledAt(LocalDateTime.now());

        BanAction cancelAction = banActionService.logAction(ban,
                request.getExecutingUserId(),
                oldValue,
                request.getComment());

        ban.addAction(cancelAction);

        log.info("Bann {} aufgehoben: {}", banId, cancelAction);
        return ban;
    }

    @NonNull
    private Ban buildBan(User user, LocalDateTime endAt, BanReason reason, String internalNote, Long createdByUserId) {
        return Objects.requireNonNull(Ban.builder()
                .user(user)
                .startAt(LocalDateTime.now())
                .endAt(endAt)
                .reason(reason)
                .internalNote(internalNote)
                .build());
    }
}
