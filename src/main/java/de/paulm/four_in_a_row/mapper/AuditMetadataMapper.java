package de.paulm.four_in_a_row.mapper;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.web.dtos.Audit;
import de.paulm.model.AuditMetadataWdto;

@Mapper(componentModel = "spring", uses = { JsonNullableMapper.class })
public interface AuditMetadataMapper {

    AuditMetadataWdto toMetadataWdto(Audit<?> audit);
}
