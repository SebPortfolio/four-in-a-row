package de.paulm.four_in_a_row.domain.security;

import java.time.LocalDateTime;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.paulm.four_in_a_row.domain.security.annotations.ValidDateRange;
import de.paulm.four_in_a_row.domain.security.interfaces.DateTimeRange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Table(name = "BAN")
@ValidDateRange
public class Ban extends LastModified implements DateTimeRange<LocalDateTime> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "START_AT", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "END_AT")
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "REASON", nullable = false)
    private BanReason reason;

    @Column(name = "INTERNAL_NOTE", nullable = false)
    private String internalNote;

    @Column(name = "CANCELLED_AT")
    private LocalDateTime cancelledAt; // Wenn gesetzt, ist der Ban hinfällig

    @Column(name = "LAST_INTERNAL_COMMENT", nullable = false)
    private String lastInternalComment;

    public boolean isActive() {
        if (cancelledAt != null) {
            return false; // Ban aufgehoben
        }
        if (this.endAt == null) {
            return true; // Perma-Ban
        }
        LocalDateTime now = LocalDateTime.now();
        return !this.startAt.isAfter(now) && this.endAt.isAfter(now);
    }

}
