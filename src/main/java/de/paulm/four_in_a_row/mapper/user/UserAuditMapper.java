package de.paulm.four_in_a_row.mapper.user;

import java.util.List;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.mapper.AuditMetadataMapper;
import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.web.dtos.Audit;
import de.paulm.model.UserAuditWdto;

@Mapper(componentModel = "spring", uses = {
        AuditMetadataMapper.class, UserMapper.class, JsonNullableMapper.class
})
public interface UserAuditMapper {

    UserAuditWdto toWdto(Audit<User> object);

    List<UserAuditWdto> toWdtoList(List<Audit<User>> history);
}
