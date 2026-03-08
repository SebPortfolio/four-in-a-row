package de.paulm.four_in_a_row.mapper.user;

import java.util.List;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminResponse;
import de.paulm.model.UserAdminCreateRequestWdto;
import de.paulm.model.UserAdminPatchRequestWdto;
import de.paulm.model.UserAdminWdto;

@Mapper(config = UserMappingConfig.class, uses = { SharedSecurityMapper.class, JsonNullableMapper.class })
public interface UserAdminMapper {

    UserAdminWdto toResponseWdto(UserAdminResponse object);

    List<UserAdminWdto> toResponseWdtoList(List<UserAdminResponse> objects);

    UserAdminCreateRequest fromCreateRequestWdto(UserAdminCreateRequestWdto wdto);

    UserAdminPatchRequest fromPatchRequestWdto(UserAdminPatchRequestWdto wdto);
}
