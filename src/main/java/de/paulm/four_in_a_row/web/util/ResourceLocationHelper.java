package de.paulm.four_in_a_row.web.util;

import java.net.URI;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ResourceLocationHelper {
    /**
     * Erzeugt eine URI für eine neu erstellte Ressource.
     * 
     * @param id               Die ID der neuen Ressource
     * @param pathVariableName Der Name der Variable im Path (z.B. "gameId")
     * @return URI für den Location Header
     */
    @NonNull
    public static URI create(Object id, String pathVariableName) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ " + pathVariableName + "}")
                .buildAndExpand(id)
                .toUri();
    }
}
