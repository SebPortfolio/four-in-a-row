package de.paulm.four_in_a_row.mapper.user;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.UserProjection;
import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.four_in_a_row.web.dtos.UserAdminCreateRequest;
import de.paulm.four_in_a_row.web.dtos.UserAdminPatchRequest;
import de.paulm.model.UserAdminCreateRequestWdto;
import de.paulm.model.UserAdminPatchRequestWdto;
import de.paulm.model.UserAdminResponseWdto;

@Mapper(config = UserMappingConfig.class, uses = { SharedSecurityMapper.class, JsonNullableMapper.class })
public interface UserAdminMapper {

    @Mapping(target = "customPermissions", source = "user.customPermissions")
    UserAdminResponseWdto toResponseWdto(UserProjection object);

    List<UserAdminResponseWdto> toResponseWdtoList(List<UserProjection> objects);

    UserAdminCreateRequest fromCreateRequestWdto(UserAdminCreateRequestWdto wdto);

    UserAdminPatchRequest fromPatchRequestWdto(UserAdminPatchRequestWdto wdto);
}
