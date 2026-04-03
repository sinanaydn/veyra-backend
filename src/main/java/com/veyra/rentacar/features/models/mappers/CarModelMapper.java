package com.veyra.rentacar.features.models.mappers;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.veyra.rentacar.features.models.dtos.requests.CreateCarModelRequest;
import com.veyra.rentacar.features.models.dtos.requests.UpdateCarModelRequest;
import com.veyra.rentacar.features.models.dtos.responses.GetCarModelResponse;
import com.veyra.rentacar.features.models.entities.CarModel;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarModelMapper {

    @Mapping(target = "brand.id", source = "brandId")
    CarModel toEntity(CreateCarModelRequest request);

    @Mapping(target = "brandName", source = "brand.name")
    GetCarModelResponse toResponse(CarModel carModel);

    List<GetCarModelResponse> toResponseList(List<CarModel> models);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromRequest(UpdateCarModelRequest request, @MappingTarget CarModel carModel);
}
