package de.paulm.four_in_a_row.mapper.user;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.mapper.ban.BanMapper;
import de.paulm.model.MyProfileWdto;

@Mapper(componentModel = "spring", uses = { JsonNullableMapper.class, BanMapper.class })
public interface MyProfileMapper {

    MyProfileWdto toWdto(User user, String displayName);
}
