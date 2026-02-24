package de.paulm.four_in_a_row.mapper;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.AuthUserResponse;
import de.paulm.model.AuthUserResponseWdto;

@Mapper(componentModel = "spring", uses = UserContextResponseMapper.class)
public interface AuthUserResponseMapper {

    AuthUserResponseWdto toWdto(AuthUserResponse record);
}
