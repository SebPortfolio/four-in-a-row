package de.paulm.four_in_a_row.domain.security;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Table(name = "APP_USER") // User ist SQL-Keyword
public class User implements UserDetails {

    public static final int EMAIL_MAX_LENGTH = 254;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "Email darf nicht leer sein")
    @Email(message = "Bitte eine gültige Email-Adresse angeben")
    @Size(max = EMAIL_MAX_LENGTH, message = "Email darf nicht mehr als" + EMAIL_MAX_LENGTH + " Zeichen haben")
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "LAST_PASSWORD_CHANGE")
    private LocalDateTime lastPasswordChangeAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USER_ID"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_PERMISSIONS", joinColumns = @JoinColumn(name = "USER_ID"))
    @Enumerated(EnumType.STRING)
    private Set<Permission> customPermissions;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    @Builder.Default
    private UserStatus status = UserStatus.UNVERIFIED;

    @Column(name = "BANNED_UNTIL")
    private LocalDateTime bannedUntil;

    @Column(name = "INTERNAL_BAN_NOTE")
    private String internalBanNote; // Für das Admin-Team (was genau ist passiert?)

    @Enumerated(EnumType.STRING)
    @Column(name = "BAN_REASON")
    private BanReason banReason; // für User

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (status == UserStatus.PERMANENT_BANNED) {
            return false;
        }
        if (status == UserStatus.BANNED) {
            return bannedUntil != null && bannedUntil.isBefore(LocalDateTime.now());
        }

        return status != UserStatus.DELETED;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != UserStatus.DELETED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());

        roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getValue()))
                .forEach(authorities::add);

        if (customPermissions != null) {
            customPermissions.stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getValue()))
                    .forEach(authorities::add);
        }

        return authorities;
    }
}