package de.paulm.four_in_a_row.web.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import de.paulm.four_in_a_row.domain.security.Ban;
import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.domain.security.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAdminResponse {
    private Long id;
    private String email;
    private Set<Role> roles;
    private Set<Permission> customPermissions;
    private LocalDateTime lastPasswordChangeAt;
    private UserStatus status;
    private Ban activeBan;
    private List<Ban> banHistory;
    private String displayName;
}
