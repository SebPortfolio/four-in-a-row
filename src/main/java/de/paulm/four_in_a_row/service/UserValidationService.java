package de.paulm.four_in_a_row.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.stereotype.Service;

import de.paulm.four_in_a_row.domain.exceptions.IllegalEmailException;
import de.paulm.four_in_a_row.domain.exceptions.WeakPasswordException;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;
    private final EmailValidator emailValidator = new EmailValidator();

    private static final Pattern LOWER_CASE = Pattern.compile(".*[a-z].*");
    private static final Pattern UPPER_CASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR = Pattern.compile(".*[@$!%*?&\\.#\\-_].*");

    public void validateEmail(String email, Long userId) throws IllegalEmailException {
        if (email == null || email.isBlank()) {
            throw new IllegalEmailException(email, "null oder leer");
        }
        if (!emailValidator.isValid(email, null)) {
            throw new IllegalEmailException(email, "ungültiges E-Mail Format");
        }
        if (email.length() > User.EMAIL_MAX_LENGTH) {
            throw new IllegalEmailException(email, "zu lang, maximal " + User.EMAIL_MAX_LENGTH + " Zeichen");
        }
        if (userId == null && userRepository.existsByEmail(email)) {
            throw new IllegalEmailException(email, "undefiniertes Problem");

        } else if (userId != null && userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new IllegalEmailException(email, "undefiniertes Problem");
            // TODO: nur für Admins die klare Fehlerursache
            // es darf keine doppelten Mails geben, aber es soll nicht so transparent sein,
            // dass diese email existiert und auch hier registriert ist
        }
    }

    public void validatePassword(String rawPassword) {
        List<String> missingRequirements = new ArrayList<>();
        if (rawPassword == null || rawPassword.isBlank()) {
            missingRequirements.add("LENGTH");
            throw new WeakPasswordException(missingRequirements);
        }

        if (rawPassword.length() < 8) {
            missingRequirements.add("LENGTH");
        }
        if (!LOWER_CASE.matcher(rawPassword).matches()) {
            missingRequirements.add("LOWER_CASE");
        }
        if (!UPPER_CASE.matcher(rawPassword).matches()) {
            missingRequirements.add("UPPER_CASE");
        }
        if (!DIGIT.matcher(rawPassword).matches()) {
            missingRequirements.add("DIGIT");
        }
        if (!SPECIAL_CHAR.matcher(rawPassword).matches()) {
            missingRequirements.add("SPECIAL_CHAR");
        }

        if (!missingRequirements.isEmpty()) {
            throw new WeakPasswordException(missingRequirements);
        }
    }
}
