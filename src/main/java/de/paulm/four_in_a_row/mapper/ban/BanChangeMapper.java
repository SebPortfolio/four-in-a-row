package de.paulm.four_in_a_row.mapper.ban;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.web.dtos.ban.BanCreateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.BanPermanentCreateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.BanUpdateRequest;
import de.paulm.four_in_a_row.web.dtos.ban.CancelBanRequest;
import de.paulm.model.BanCreateRequestWdto;
import de.paulm.model.BanPermanentCreateRequestWdto;
import de.paulm.model.BanUpdateRequestWdto;
import de.paulm.model.CancelBanRequestWdto;

@Mapper(componentModel = "spring", uses = { JsonNullableMapper.class })
public interface BanChangeMapper {

    BanCreateRequest fromBanCreateRequestWdto(BanCreateRequestWdto wdto);

    BanPermanentCreateRequest fromBanCreatePermanentRequestWdto(BanPermanentCreateRequestWdto wdto);

    BanUpdateRequest fromBanUpdateRequestWdto(BanUpdateRequestWdto wdto);

    CancelBanRequest fromCancelBanRequestWdto(CancelBanRequestWdto wdto);
}
