package de.paulm.four_in_a_row.mapper;

import org.mapstruct.Mapper;

import de.paulm.four_in_a_row.web.dtos.RegisterRequest;
import de.paulm.model.RegisterRequestWdto;

@Mapper
public interface RegisterRequestMapper {
    RegisterRequest fromWdto(RegisterRequestWdto wdto);
}
