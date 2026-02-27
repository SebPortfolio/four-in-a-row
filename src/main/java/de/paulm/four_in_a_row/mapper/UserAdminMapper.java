package de.paulm.four_in_a_row.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.UserProfileAggregate;
import de.paulm.model.UserAdminWdto;

@Mapper(config = UserMappingConfig.class)
public interface UserAdminMapper extends SharedSecurityMapper {

    @Mapping(target = "user.customPermissions", source = "customPermissions")
    @Mapping(target = "user.internalBanNote", source = "internalBanNote")
    UserProfileAggregate fromWdto(UserAdminWdto wdto);

    @Mapping(target = "customPermissions", source = "user.customPermissions")
    @Mapping(target = "internalBanNote", source = "user.internalBanNote")
    UserAdminWdto toWdto(UserProfileAggregate record);

    List<UserProfileAggregate> fromWdtoList(List<UserAdminWdto> wdtos);

    List<UserAdminWdto> toWdtoList(List<UserProfileAggregate> records);
}
