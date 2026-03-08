package de.paulm.four_in_a_row.web.dtos;

import java.util.Set;

import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAdminPatchRequest {
    @Size(min = 3)
    @Email
    private String email;
    @Size(min = 3)
    private String displayName;
    private Set<Role> roles;
    private Set<Permission> customPermissions;
}