package de.paulm.four_in_a_row.mapper.user;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.mapper.SharedSecurityMapper;
import de.paulm.four_in_a_row.web.dtos.UserAdminOverviewResponse;
import de.paulm.model.UserAdminOverviewResponseWdto;

@Mapper(componentModel = "spring", uses = { SharedSecurityMapper.class })
public interface UserAdminOverviewMapper {
    @Mapping(source = "banned", target = "isBanned")
    UserAdminOverviewResponseWdto toWdto(UserAdminOverviewResponse dto);

    List<UserAdminOverviewResponseWdto> toWdtoList(List<UserAdminOverviewResponse> dtos);
}
