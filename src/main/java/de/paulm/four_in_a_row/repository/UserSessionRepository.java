package de.paulm.four_in_a_row.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.paulm.four_in_a_row.domain.security.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findAllByUserId(Long userId);

    List<UserSession> findAllByUserIdAndInvalidatedFalse(Long userId);

    Optional<UserSession> findByRefreshToken(String RefreshToken);

    Void deleteAllByUserId(Long userId);

    Void deleteByUserIdAndRefreshTokenNot(Long userId, String refreshToken);

    Void deleteByRefreshToken(String refreshToken);

}
