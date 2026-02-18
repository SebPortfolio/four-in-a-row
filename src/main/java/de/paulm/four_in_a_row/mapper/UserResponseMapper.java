package de.paulm.four_in_a_row.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.model.UserResponseWdto;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {

    @Mapping(source = "authorities", target = "roles")
    UserResponseWdto toWdto(User user);
}
