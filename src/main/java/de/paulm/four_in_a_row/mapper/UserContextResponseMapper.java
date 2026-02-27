package de.paulm.four_in_a_row.mapper;

import java.util.List;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.model.UserContextResponseWdto;

@Mapper(componentModel = "spring")
public interface UserContextResponseMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "playerId", source = "player.id")
    @Mapping(target = "displayName", source = "player.displayName")
    @Mapping(target = "allPermissions", expression = "java(concatAndMapPermissions(userContextResponse.user()))")
    UserContextResponseWdto toWdto(UserProfileAggregate userContextResponse);

    default List<String> concatAndMapPermissions(User user) {
        if (user == null) {
            return List.of();
        }

        Stream<String> rolePermissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getValue);

        Stream<String> customPermissions = user.getCustomPermissions().stream()
                .map(Permission::getValue);

        Stream<String> roleNames = user.getRoles().stream()
                .map(Role::name);

        return Stream.concat(Stream.concat(rolePermissions, customPermissions), roleNames)
                .distinct()
                .toList();
    }
}
