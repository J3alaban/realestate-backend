package com.realestate.backend.mappers;

import com.realestate.backend.dtos.requests.DimensionsRequest;
import com.realestate.backend.dtos.responses.DimensionsResponse;
import com.realestate.backend.entities.Dimensions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DimensionsMapper {

    Dimensions toEntity(DimensionsRequest request);

    DimensionsResponse toResponse(Dimensions dimensions);
}