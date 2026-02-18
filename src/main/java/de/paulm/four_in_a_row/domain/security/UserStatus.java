package de.paulm.four_in_a_row.domain.security;

public enum UserStatus {
    ACTIVE,
    UNVERIFIED, // registriert, wartet auf E-Mail-Bestätigung
    BANNED, // temporät gebannt (Fehlverhalten etc.)
    PERMANENT_BANNED, // permanent gebannt (Hacking, etc.)
    DELETED // soft-deleted / anonymisiert
}
