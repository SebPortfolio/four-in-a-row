package de.paulm.four_in_a_row.domain.security;

public enum BanReason {
    INAPPROPRIATE_USERNAME("Dein Benutzername verstößt gegen unsere Richtlinien."),
    CHEATING("Es wurden unzulässige Modifikationen oder Verhaltensweisen erkannt."),
    BEHAVIOR("Dein Verhalten gegenüber anderen Spielern war unangemessen."),
    SPAM("Dein Account wurde wegen Spam-Aktivitäten markiert."),
    OTHER("Dein Account wurde aufgrund eines Verstoßes gesperrt.");

    private final String description;

    BanReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}