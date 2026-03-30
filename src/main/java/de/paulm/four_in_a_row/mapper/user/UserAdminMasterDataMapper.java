package de.paulm.four_in_a_row.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.mapper.LastModifiedInfosMapper;
import de.paulm.four_in_a_row.web.dtos.UserAdminMasterDataResponse;
import de.paulm.model.UserAdminMasterDataResponseWdto;

@Mapper(componentModel = "spring", uses = { JsonNullableMapper.class, LastModifiedInfosMapper.class })
public interface UserAdminMasterDataMapper {

    @Mapping(target = "lastModifiedInfos", source = ".")
    UserAdminMasterDataResponseWdto toWdto(UserAdminMasterDataResponse dto);
}
