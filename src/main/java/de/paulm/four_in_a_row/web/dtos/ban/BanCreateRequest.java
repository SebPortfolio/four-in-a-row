package de.paulm.four_in_a_row.web.dtos.ban;

import java.time.LocalDateTime;

import de.paulm.four_in_a_row.domain.security.BanReason;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BanCreateRequest {
    @NotNull
    private Long executingUserId;
    @NotNull
    private BanReason reason;
    @NotBlank
    private String internalNote;
    @NotNull
    @FutureOrPresent
    private LocalDateTime endAt;
}
