package de.paulm.four_in_a_row.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.RegistrationException;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.model.AuthResponseWdto;
import de.paulm.model.LoginRequestWdto;
import de.paulm.model.RegisterRequestWdto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PlayerProfileService playerProfileService;

    @Transactional
    public AuthResponseWdto register(RegisterRequestWdto request) {
        if (userService.existsByEmail(request.getEmail())) {
            // TODO: Bestätigungsmail an bestehenden User,
            // ob er sich neu registrieren wollte?
            throw new RegistrationException("Ein Account mit dieser Email ist bereits vergeben!");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = userService.buildNewUser(
                request.getEmail(),
                encodedPassword);

        user = userService.saveUser(user);

        playerProfileService.createProfileForUser(user.getId(), request.getDisplayName());

        String token = jwtService.generateToken(user);
        return new AuthResponseWdto().token(token);
    }

    public AuthResponseWdto authenticate(LoginRequestWdto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails user = userService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(user);
        return new AuthResponseWdto().token(token);
    }
}