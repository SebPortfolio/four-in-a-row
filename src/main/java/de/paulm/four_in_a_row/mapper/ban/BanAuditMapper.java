package de.paulm.four_in_a_row.mapper.ban;

import java.util.List;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.Ban;
import de.paulm.four_in_a_row.mapper.AuditMetadataMapper;
import de.paulm.four_in_a_row.mapper.JsonNullableMapper;
import de.paulm.four_in_a_row.web.dtos.Audit;
import de.paulm.model.BanAuditWdto;

@Mapper(componentModel = "spring", uses = { AuditMetadataMapper.class, BanMapper.class, JsonNullableMapper.class })
public interface BanAuditMapper {
    BanAuditWdto toWdto(Audit<Ban> entity);

    List<BanAuditWdto> toWdtoList(List<Audit<Ban>> entities);
}
