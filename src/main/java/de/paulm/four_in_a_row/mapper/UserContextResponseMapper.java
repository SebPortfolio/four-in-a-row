package de.paulm.four_in_a_row.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.UserContextResponse;
import de.paulm.model.UserContextResponseWdto;

@Mapper(componentModel = "spring")
public interface UserContextResponseMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "playerId", source = "playerProfile.id")
    @Mapping(target = "displayName", source = "playerProfile.displayName")
    UserContextResponseWdto toWdto(UserContextResponse userContextResponse);
}
