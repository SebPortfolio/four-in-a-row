package de.paulm.four_in_a_row.mapper.ban;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.Ban;
import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.model.BanWdto;

@Mapper(componentModel = "spring", uses = { JsonNullableMapper.class, BanActionMapper.class })
public interface BanMapper {

    @Mapping(target = "user.id", source = "userId")
    Ban fromBanWdto(BanWdto wdto);

    @Mapping(target = "userId", source = "user.id")
    BanWdto toBanWdto(Ban entity);
}
