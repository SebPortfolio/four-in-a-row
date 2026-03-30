package de.paulm.four_in_a_row.web.dtos;

import java.time.LocalDateTime;
import java.util.Set;

import de.paulm.four_in_a_row.domain.security.ILastModified;
import de.paulm.four_in_a_row.domain.security.Permission;
import de.paulm.four_in_a_row.domain.security.Role;
import de.paulm.four_in_a_row.domain.security.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAdminMasterDataResponse implements ILastModified {
    private Long id;
    private String displayName;
    private String maskedEmail;
    private LocalDateTime lastPasswordChangeAt;
    private UserStatus status;
    private Set<Role> roles;
    private Set<Permission> customPermissions;
    private LocalDateTime lastModifiedAt;
    private Long lastModifiedByUserId;
    private String lastModifiedByDisplayName;
}
