package de.paulm.four_in_a_row.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.MappingTarget;

import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;

@MapperConfig(componentModel = "spring", mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG)
public interface UserMappingConfig {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "lastPasswordChangeAt", source = "user.lastPasswordChangeAt")
    @Mapping(target = "roles", source = "user.roles")
    @Mapping(target = "status", source = "user.status")
    @Mapping(target = "bannedUntil", source = "user.bannedUntil")
    @Mapping(target = "banReason", source = "user.banReason")
    @Mapping(target = "displayName", source = "player.displayName")
    @Mapping(target = "statistic", ignore = true)
    void commonToWdto(UserProfileAggregate source, @MappingTarget Object target);

    @Mapping(target = "player.displayName", source = "displayName")
    @Mapping(target = "player.userId", source = "id")
    @Mapping(target = "user.id", source = "id")
    @Mapping(target = "user.email", source = "email")
    @Mapping(target = "user.lastPasswordChangeAt", source = "lastPasswordChangeAt")
    @Mapping(target = "user.roles", source = "roles")
    @Mapping(target = "user.status", source = "status")
    @Mapping(target = "user.bannedUntil", source = "bannedUntil")
    @Mapping(target = "user.banReason", source = "banReason")
    @Mapping(target = "user.password", ignore = true)
    void commonFromWdto(Object source, @MappingTarget UserProfileAggregate target);
}
