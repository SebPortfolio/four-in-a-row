package de.paulm.four_in_a_row.web.util;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;

import java.net.URI;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ResourceLocationHelper {

    /**
     * Erzeugt eine URI für eine neu erstellte Ressource.
     * 
     * @param id            Die ID der neuen Ressource
     * @param pathParamName Der Name des Parameters im Path (z.B. "gameId")
     * @return URI für den Location Header
     */
    @NonNull
    public static URI create(Object id, String pathParamName) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ " + pathParamName + "}")
                .buildAndExpand(id)
                .toUri();
    }

    /**
     * Erzeugt eine URI basierend auf einer Controller-Methode.
     * Nutzt Reflection, um die Pfade aus den @RequestMapping Annotationen der
     * Delegates zu lesen.
     *
     * @param methodCall Ein Proxy-Aufruf via MvcUriComponentsBuilder.on()
     * @return Die absolute URI der Ziel-Ressource
     */
    @NonNull
    public static <T> URI createFromMethod(T methodCall) {
        return fromMethodCall(Objects.requireNonNull(methodCall))
                .build()
                .toUri();
    }

}
