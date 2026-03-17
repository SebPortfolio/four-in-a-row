package de.paulm.four_in_a_row.mapper.user;

import java.util.List;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.model.UserResponseWdto;

@Mapper(config = UserMappingConfig.class, uses = { SharedSecurityMapper.class, JsonNullableMapper.class })
public interface UserMapper {
    UserResponseWdto toWdto(User entity);

    List<UserResponseWdto> toWdtoList(List<User> entities);

}
