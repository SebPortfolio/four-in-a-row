package de.paulm.four_in_a_row.domain.security;

public enum UserStatus {
    ACTIVE,
    UNVERIFIED, // registriert, wartet auf E-Mail-Bestätigung
    DEACTIVATED
    // TODO: zusätzlicher Status für Aktiv, aber immer noch Einmalpasswort
}
