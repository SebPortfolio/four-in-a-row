package de.paulm.four_in_a_row.domain.security;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
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

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USER_ID"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "USER_PERMISSIONS", joinColumns = @JoinColumn(name = "USER_ID"))
    @Enumerated(EnumType.STRING)
    private Set<Permission> customPermissions = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.UNVERIFIED;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startAt DESC")
    @Builder.Default
    private List<Ban> banHistory = new ArrayList<>();

    @Column(name = "PLAYER_ID", unique = true)
    private Long playerId;

    public Ban getActiveBan() {
        return banHistory.stream()
                .filter(Ban::isActive)
                .sorted((b1, b2) -> {
                    // Perma-Ban hat Vorrang
                    if (b1.getEndAt() == null) {
                        return -1;
                    }
                    if (b2.getEndAt() == null) {
                        return 1;
                    }
                    // Ansonsten der Ban, der länger gültig ist
                    return b2.getEndAt().compareTo(b1.getEndAt());
                })
                .findFirst()
                .orElse(null);
    }

    public void addBan(@Valid Ban ban) {
        ban.setUser(this);
        this.banHistory.add(ban);
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return getActiveBan() == null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    // @JsonIgnore // ggf. für Sicherheit
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