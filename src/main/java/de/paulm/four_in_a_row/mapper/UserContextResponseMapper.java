package de.paulm.four_in_a_row.mapper;

import java.util.List;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserContextResponse;
import de.paulm.model.UserContextResponseWdto;

@Mapper(componentModel = "spring")
public interface UserContextResponseMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "playerId", source = "playerProfile.id")
    @Mapping(target = "displayName", source = "playerProfile.displayName")
    @Mapping(target = "allPermissions", expression = "java(concatAndMapPermissions(userContextResponse.user()))")
    UserContextResponseWdto toWdto(UserContextResponse userContextResponse);

    default List<String> concatAndMapPermissions(User user) {
        if (user == null)
            return List.of();

        return Stream
                .concat(user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream()),
                        user.getCustomPermissions().stream())
                .map(permission -> permission.getValue())
                .toList();
    }
}
