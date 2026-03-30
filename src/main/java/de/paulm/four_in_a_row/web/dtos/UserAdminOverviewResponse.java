package de.paulm.four_in_a_row.web.dtos;

import java.util.Set;

import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.domain.security.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAdminOverviewResponse {
    private Long id;
    private String displayName;
    private String maskedEmail;
    private UserStatus status;
    private Set<Role> roles;
    private boolean hasCustomPermissions;
    private boolean banned;
}
