package de.paulm.four_in_a_row.mapper.ban;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.BanReason;
import de.paulm.model.BanReasonWdto;

@Mapper(componentModel = "spring")
public interface BanReasonMapper {
    BanReason fromWdto(BanReasonWdto wdto);

    BanReasonWdto toWdto(BanReason reason);
}
