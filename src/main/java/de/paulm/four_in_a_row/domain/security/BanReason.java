package de.paulm.four_in_a_row.domain.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BanReason {
    // description dient nur für Logs
    INAPPROPRIATE_USERNAME("Der Benutzername verstößt gegen unsere Richtlinien."),
    CHEATING("Es wurden unzulässige Modifikationen oder Verhaltensweisen erkannt."),
    BEHAVIOR("Das Verhalten gegenüber anderen Spielern war unangemessen."),
    SPAM("Es wurden Spam-Aktivitäten des Accounts registriert."),
    OTHER("Der Account wurde aufgrund eines Verstoßes gesperrt.");

    private final String description;
}