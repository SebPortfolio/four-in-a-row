package de.paulm.four_in_a_row.mapper.user;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.AuthResponse;
import de.paulm.model.AuthResponseWdto;

@Mapper(componentModel = "spring")
public interface AuthResponseMapper {

    AuthResponseWdto toWdto(AuthResponse record);
}
