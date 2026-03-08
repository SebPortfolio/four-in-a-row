package de.paulm.four_in_a_row.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;

@Mapper(componentModel = "spring")
public interface SharedSecurityMapper {

    Set<Permission> toPermissionSet(List<String> permStrings);

    Set<Role> toRoleSet(List<String> roleStrings);

    List<String> fromPermissionArray(Permission[] permissions);

    List<String> fromRoleArray(Role[] roles);

    List<String> fromPermissionList(List<Permission> permissions);

    List<String> fromRoleList(List<Role> roles);

    default Role mapStringToRole(String name) {
        return name == null ? null : Role.valueOf(name);
    }

    default String mapRoleToString(Role role) {
        return role == null ? null : role.name();
    }

    default Permission mapStringToPermission(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(Permission.values())
                .filter(p -> p.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown permission: " + value));
    }

    default String mapPermissionToString(Permission perm) {
        return perm == null ? null : perm.getValue();
    }
}
