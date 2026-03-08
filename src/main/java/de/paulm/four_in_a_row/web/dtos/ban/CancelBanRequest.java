package de.paulm.four_in_a_row.web.dtos.ban;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelBanRequest {
    @NotNull
    private Long executingUserId;
    @NotBlank
    private String comment;
}
