package de.paulm.four_in_a_row.mapper.user;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.MappingTarget;

import de.paulm.four_in_a_row.domain.security.UserProjection;
import de.paulm.four_in_a_row.mapper.ban.BanMapper;

@MapperConfig(componentModel = "spring", mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG, uses = {
        BanMapper.class })
public interface UserMappingConfig {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "lastPasswordChangeAt", source = "user.lastPasswordChangeAt")
    @Mapping(target = "roles", source = "user.roles")
    @Mapping(target = "status", source = "user.status")
    @Mapping(target = "activeBan", source = "user.activeBan")
    @Mapping(target = "bans", source = "user.bans")
    @Mapping(target = "playerId", source = "user.playerId")
    void commonToWdto(UserProjection source, @MappingTarget Object target);

    @Mapping(target = "user.id", source = "id")
    @Mapping(target = "user.email", source = "email")
    @Mapping(target = "user.lastPasswordChangeAt", source = "lastPasswordChangeAt")
    @Mapping(target = "user.roles", source = "roles")
    @Mapping(target = "user.status", source = "status")
    @Mapping(target = "user.banHistory", source = "banHistory")
    @Mapping(target = "user.password", ignore = true)
    void commonFromWdto(Object source, @MappingTarget UserProjection target);
}
