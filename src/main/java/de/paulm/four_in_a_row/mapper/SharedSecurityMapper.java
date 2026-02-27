package de.paulm.four_in_a_row.mapper;

import java.util.Arrays;

import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;

public interface SharedSecurityMapper {

    default Role mapToRole(String name) {
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
