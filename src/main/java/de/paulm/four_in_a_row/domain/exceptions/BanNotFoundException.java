package de.paulm.four_in_a_row.domain.exceptions;

import de.paulm.four_in_a_row.web.exceptions.advice.ResourceNotFoundException;

public class BanNotFoundException extends ResourceNotFoundException {
    public BanNotFoundException(Long id) {
        super("Kein Bann mit der Id " + id + " gefunden", "BAN_NOT_FOUND", "id");
    }
}
