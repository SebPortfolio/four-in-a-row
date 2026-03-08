package de.paulm.four_in_a_row.web.dtos;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Email
    private String email;
    private String displayName;
}
