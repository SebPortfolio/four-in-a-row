package de.paulm.four_in_a_row.web.handler;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import de.paulm.api.AuthApiDelegate;
import de.paulm.four_in_a_row.domain.security.AuthUserResponse;
import de.paulm.four_in_a_row.domain.security.UserSession;
import de.paulm.four_in_a_row.mapper.RegisterRequestMapper;
import de.paulm.four_in_a_row.mapper.user.AuthUserResponseMapper;
import de.paulm.four_in_a_row.mapper.user.UserSessionMapper;
import de.paulm.four_in_a_row.service.AuthenticationService;
import de.paulm.four_in_a_row.service.UserSessionService;
import de.paulm.four_in_a_row.web.dtos.RegisterRequest;
import de.paulm.model.AuthUserResponseWdto;
import de.paulm.model.LoginRequestWdto;
import de.paulm.model.PasswordChangeRequestWdto;
import de.paulm.model.RegisterRequestWdto;
import de.paulm.model.UserContextResponseWdto;
import de.paulm.model.UserSessionWdto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j // FIXME: entfernen !!!
public class AuthApiHandler implements AuthApiDelegate {

    private final AuthenticationService authService;
    private final UserSessionService userSessionService;
    private final UserSessionMapper userSessionMapper;
    private final AuthUserResponseMapper authUserResponseMapper;
    private final RegisterRequestMapper registerRequestMapper;

    @Value("${application.security.cookie-secure}")
    private boolean cookieSecure;

    @Override
    public ResponseEntity<AuthUserResponseWdto> register(RegisterRequestWdto requestWdto) {
        log.info("Registrieren mit {} als {}", requestWdto.getEmail(), requestWdto.getDisplayName());
        HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String ipAddress = this.determineIpAddress(servletRequest);
        RegisterRequest registerRequest = registerRequestMapper.fromWdto(requestWdto);

        AuthUserResponse responseRecord = authService.register(registerRequest, ipAddress,
                servletRequest.getHeader("User-Agent"));
        AuthUserResponseWdto responseWdto = authUserResponseMapper.toWdto(responseRecord);

        ResponseCookie springCookie = buildRefreshTokenCookie(responseRecord.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(responseWdto);
    }

    @Override
    public ResponseEntity<AuthUserResponseWdto> login(LoginRequestWdto requestWdto) {
        log.info("Login mit {}", requestWdto.getEmail());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String oldToken = extractRefreshTokenCookie(request);
        String ipAddress = this.determineIpAddress(request);

        AuthUserResponse responseRecord = authService.login(requestWdto.getEmail(), requestWdto.getPassword(),
                ipAddress, request.getHeader("User-Agent"), oldToken);
        AuthUserResponseWdto responseWdto = authUserResponseMapper.toWdto(responseRecord);

        ResponseCookie springCookie = buildRefreshTokenCookie(responseRecord.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(responseWdto);
    }

    @Override
    public ResponseEntity<AuthUserResponseWdto> refreshAccessToken(String refreshToken) {
        log.info("erneuere Session");

        AuthUserResponse record = authService.refreshSession(refreshToken);
        AuthUserResponseWdto wdto = authUserResponseMapper.toWdto(record);

        ResponseCookie springCookie = buildRefreshTokenCookie(record.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(wdto);
    }

    @Override
    public ResponseEntity<Void> logout(String refreshToken) {
        userSessionService.logoutByRefreshToken(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/api/v1/auth")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @Override
    public ResponseEntity<UserContextResponseWdto> me() {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> changePassword(String refreshToken, PasswordChangeRequestWdto requestWdto) {
        authService.changePassword(requestWdto.getOldPassword(), requestWdto.getNewPassword(), refreshToken);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UserSessionWdto>> getActiveSessions(String refreshToken) {
        List<UserSession> activeSessions = userSessionService.getActiveSessionsByUserId(null); // TODO: userId bestimmen
        List<UserSessionWdto> activeSessionWdtos = userSessionMapper.toWdtoLost(activeSessions, refreshToken);
        return ResponseEntity.ok(activeSessionWdtos);
    }

    @Override
    public ResponseEntity<Void> logoutOthers(String refreshToken) {
        userSessionService.logoutEverywhereButCurrent(null, refreshToken);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> terminateSession(String sessionId) {
        try {
            Long castedSessionId = Long.valueOf(sessionId);
            Objects.requireNonNull(castedSessionId, "Keine gültige Session-Id");
            userSessionService.logoutBySessionId(castedSessionId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Keine numerische Session-Id: " + sessionId, ex);
        }
        return ResponseEntity.noContent().build();
    }

    private String determineIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        // Falls X-Forwarded-For mehrere IPs enthält (Komma-getrennt), die erste nehmen
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        log.info("IP-Adresse: {}", ipAddress);
        return ipAddress;
    }

    private String extractRefreshTokenCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        // Refresh Token in ein HttpOnly-Cookie legen
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/v1/auth")
                .maxAge(604800) // 7 Tage
                .sameSite("Strict")
                .build();
    }
}
