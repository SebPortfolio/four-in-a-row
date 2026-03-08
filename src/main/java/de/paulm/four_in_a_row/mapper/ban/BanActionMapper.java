package de.paulm.four_in_a_row.mapper.ban;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paulm.four_in_a_row.domain.security.BanAction;
import de.paulm.model.BanActionWdto;

@Mapper(componentModel = "spring")
public interface BanActionMapper {
    @Mapping(target = "ban.id", source = "banId")
    BanAction fromWdto(BanActionWdto wdto);

    @Mapping(target = "banId", source = "ban.id")
    BanActionWdto toWdto(BanAction entity);
}
