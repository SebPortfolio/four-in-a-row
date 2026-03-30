package de.paulm.four_in_a_row.mapper;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.domain.security.LastModified;
import de.paulm.four_in_a_row.web.dtos.UserAdminMasterDataResponse;
import de.paulm.four_in_a_row.web.dtos.UserAdminResponse;
import de.paulm.model.LastModifiedExtendedInfosWdto;
import de.paulm.model.LastModifiedInfosWdto;

@Mapper(componentModel = "spring")
public interface LastModifiedInfosMapper {

    default LastModifiedInfosWdto toWdto(Object any) {
        if (any instanceof LastModified entity) {
            return mapBase(entity.getLastModifiedAt(), entity.getLastModifiedByUserId());
        }
        if (any instanceof UserAdminResponse dto) {
            return mapBase(dto.getLastModifiedAt(), dto.getLastModifiedByUserId());
        }

        return null;
    }

    default LastModifiedExtendedInfosWdto toExtendedWdto(Object any) {
        if (any instanceof UserAdminMasterDataResponse dto) {
            return mapExtended(dto.getLastModifiedAt(), dto.getLastModifiedByUserId(),
                    dto.getLastModifiedByDisplayName());
        }
        return null;
    }

    private LastModifiedInfosWdto mapBase(LocalDateTime date, Long id) {
        if (date == null)
            return null;
        LastModifiedInfosWdto wdto = new LastModifiedInfosWdto();
        wdto.setLastModifiedAt(date);
        wdto.setLastModifiedByUserId(id);
        return wdto;
    }

    private LastModifiedExtendedInfosWdto mapExtended(LocalDateTime date, Long id, String name) {
        if (date == null)
            return null;
        LastModifiedExtendedInfosWdto wdto = new LastModifiedExtendedInfosWdto();
        wdto.setLastModifiedAt(date);
        wdto.setLastModifiedByUserId(id);
        wdto.setLastModifiedByUserDisplayName(name);
        return wdto;
    }
}
