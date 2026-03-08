package de.paulm.four_in_a_row.mapper.user;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.model.UserWdto;

@Mapper(config = UserMappingConfig.class, uses = { SharedSecurityMapper.class })
public interface UserMapper {

    @Mapping(target = "user.customPermissions", ignore = true)
    UserProfileAggregate fromWdto(UserWdto wdto);

    UserWdto toWdto(UserProfileAggregate entity);

    List<UserProfileAggregate> fromWdtoList(List<UserWdto> wdtos);

    List<UserWdto> toWdtoList(List<UserProfileAggregate> entities);

}
