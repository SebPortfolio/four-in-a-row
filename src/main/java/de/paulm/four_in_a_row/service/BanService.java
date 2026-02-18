package de.paulm.four_in_a_row.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.security.BanReason;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserStatus;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BanService {

    @Transactional
    public void banUser(User user, int durationInHours, BanReason reason, String internalNote) {
        user.setStatus(UserStatus.BANNED);
        user.setBannedUntil(LocalDateTime.now().plusHours(durationInHours));
        user.setBanReason(reason);
        user.setInternalBanNote(internalNote);

        log.warn("User {} gebannt. Grund: {}. Notiz: {}", user.getUsername(), reason, internalNote);
    }

    @Transactional
    public void permanentlyBanUser(User user, BanReason reason, String internalNote) {
        user.setStatus(UserStatus.PERMANENT_BANNED);
        user.setBannedUntil(null); // Zeit spielt keine Rolle mehr
        user.setBanReason(reason);
        user.setInternalBanNote("PERMA-BAN: " + internalNote);

        log.error("KRITISCHER PERMA-BAN: User {} wurde permanent gesperrt. Grund: {}",
                user.getUsername(), reason);
    }

    @Transactional
    public User checkAndHandleExpiredBan(User user) {
        if (user.getStatus() == UserStatus.BANNED &&
                user.getBannedUntil() != null &&
                user.getBannedUntil().isBefore(LocalDateTime.now())) {

            log.info("Temporärer Bann für User {} ist abgelaufen. Setze Status auf ACTIVE.", user.getUsername());

            user.setStatus(UserStatus.ACTIVE);
            user.setBannedUntil(null);
        }

        return user;
    }

    public String getBanMessage(User user) {
        if (user.getStatus() != UserStatus.BANNED) {
            return "";
        }

        String template = user.getBanReason() != null ? user.getBanReason().getDescription()
                : BanReason.OTHER.getDescription();

        return String.format("%s\nDein Bann endet am: %s",
                template,
                user.getBannedUntil().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
    }
}
