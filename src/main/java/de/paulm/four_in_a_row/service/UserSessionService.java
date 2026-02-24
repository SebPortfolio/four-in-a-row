package de.paulm.four_in_a_row.service;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.UserSessionNotFoundException;
import de.paulm.four_in_a_row.domain.security.UserSession;
import de.paulm.four_in_a_row.repository.UserSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionService {

    private final UserSessionRepository sessionRepository;

    public void logoutEverywhere(Long userId) {
        sessionRepository.deleteAllByUserId(userId);
    }

    public void logoutEverywhereButCurrent(Long userId, String currentRefreshToken) {
        sessionRepository.deleteByUserIdAndRefreshTokenNot(userId, currentRefreshToken);
    }

    public void logoutByRefreshToken(String refreshToken) {
        sessionRepository.deleteByRefreshToken(refreshToken);
    }

    public void logoutBySessionId(@NonNull Long sesionId) {
        sessionRepository.deleteById(sesionId);
    }

    public List<UserSession> getAllSessionsByUserId(Long userId) {
        return sessionRepository.findAllByUserId(null);
    }

    public List<UserSession> getActiveSessionsByUserId(Long userId) {
        return sessionRepository.findAllByUserIdAndInvalidatedFalse(userId);
    }

    public UserSession getSessionByRefreshToken(String refreshToken) {
        return sessionRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new UserSessionNotFoundException(refreshToken));
    }

    @Transactional
    public String renewRefreshToken(String oldToken, String ipAddressStr, String userAgent)
            throws UserSessionNotFoundException {
        if (oldToken == null || oldToken.isBlank()) {
            throw new UserSessionNotFoundException("Kein Token übergeben");
        }
        UserSession oldSession = getSessionByRefreshToken(oldToken);
        if (checkAndHandleTokenReuse(oldSession)) {
            return null;
        }
        invalidateSession(oldSession);
        UserSession createdSession = this.createSession(oldSession.getUserId(), ipAddressStr, userAgent);
        return createdSession.getRefreshToken();
    }

    @Transactional
    public UserSession createSession(Long userId, String ipAddressStr, String userAgent) {
        String refreshToken = UUID.randomUUID().toString();
        InetAddress inetAddr = null;
        try {
            inetAddr = InetAddress.getByName(ipAddressStr);
        } catch (Exception e) {
            log.warn("IP-Adresse {} konnte nicht konvertiert werden. Grund: {}", refreshToken, e.getMessage());
        }
        UserSession builtSession = buildNewUserSession(userId, inetAddr, userAgent, refreshToken);
        return sessionRepository.save(builtSession);
    }

    @Transactional
    private UserSession invalidateSession(UserSession session) {
        session.setInvalidated(true);
        return session;
    }

    private boolean checkAndHandleTokenReuse(UserSession oldSession) {
        if (oldSession.isInvalidated()) {
            logoutEverywhere(oldSession.getUserId());
            return true;
        }
        return false;
    }

    private UserSession buildNewUserSession(Long userId, InetAddress ipAddress,
            String userAgent, String refreshToken) {
        return UserSession.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .invalidated(false)
                .createdAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }
}
