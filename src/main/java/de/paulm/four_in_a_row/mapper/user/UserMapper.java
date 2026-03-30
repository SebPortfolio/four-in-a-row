package de.paulm.four_in_a_row.mapper.user;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.mapper.LastModifiedInfosMapper;
import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.four_in_a_row.mapper.ban.BanMapper;
import de.paulm.model.UserResponseWdto;

@Mapper(uses = { BanMapper.class, LastModifiedInfosMapper.class, SharedSecurityMapper.class, JsonNullableMapper.class })
public interface UserMapper {

    @Mapping(target = "lastModifiedInfos", source = ".")
    UserResponseWdto toWdto(User entity);

    List<UserResponseWdto> toWdtoList(List<User> entities);

}
