package de.paulm.four_in_a_row.mapper.user;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.mapper.LastModifiedInfosMapper;
import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.four_in_a_row.mapper.ban.BanMapper;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminResponse;
import de.paulm.model.UserAdminCreateRequestWdto;
import de.paulm.model.UserAdminPatchRequestWdto;
import de.paulm.model.UserAdminResponseWdto;

@Mapper(uses = { BanMapper.class, LastModifiedInfosMapper.class, SharedSecurityMapper.class, JsonNullableMapper.class })
public interface UserAdminMapper {

    @Mapping(target = "lastModifiedInfos", source = "object")
    UserAdminResponseWdto toResponseWdto(UserAdminResponse object);

    List<UserAdminResponseWdto> toResponseWdtoList(List<UserAdminResponse> objects);

    UserAdminCreateRequest fromCreateRequestWdto(UserAdminCreateRequestWdto wdto);

    UserAdminPatchRequest fromPatchRequestWdto(UserAdminPatchRequestWdto wdto);
}
