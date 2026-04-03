package com.veyra.rentacar.features.cars.mappers;

import com.veyra.rentacar.features.cars.dtos.requests.CreateCarRequest;
import com.veyra.rentacar.features.cars.dtos.requests.UpdateCarRequest;
import com.veyra.rentacar.features.cars.dtos.responses.ExtraServiceResponse;
import com.veyra.rentacar.features.cars.dtos.responses.GetCarResponse;
import com.veyra.rentacar.features.cars.dtos.responses.InsurancePackageResponse;
import com.veyra.rentacar.features.cars.entities.Car;
import com.veyra.rentacar.features.cars.entities.ExtraService;
import com.veyra.rentacar.features.cars.entities.InsurancePackage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarMapper {

    @Mapping(target = "brand.id", source = "brandId")
    @Mapping(target = "model.id", source = "modelId")
    @Mapping(target = "slug", ignore = true)
    Car toEntity(CreateCarRequest request);

    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "modelName", source = "model.name")
    @Mapping(target = "category", expression = "java(car.getCategory().name())")
    @Mapping(target = "transmission", expression = "java(car.getTransmission().name())")
    @Mapping(target = "fuelType", expression = "java(car.getFuelType().name())")
    @Mapping(target = "fuelPolicy", expression = "java(car.getFuelPolicy().name())")
    @Mapping(target = "availability", expression = "java(car.getAvailability().name())")
    GetCarResponse toResponse(Car car);

    List<GetCarResponse> toResponseList(List<Car> cars);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "slug", ignore = true)
    void updateCarFromRequest(UpdateCarRequest request, @MappingTarget Car car);

    InsurancePackageResponse toInsuranceResponse(InsurancePackage pkg);

    @Mapping(target = "priceType", expression = "java(extra.getPriceType().name())")
    ExtraServiceResponse toExtraResponse(ExtraService extra);
}
