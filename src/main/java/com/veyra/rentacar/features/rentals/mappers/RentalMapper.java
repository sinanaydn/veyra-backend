package com.veyra.rentacar.features.rentals.mappers;

import com.veyra.rentacar.features.rentals.dtos.requests.CreateRentalRequest;
import com.veyra.rentacar.features.rentals.dtos.responses.GetRentalResponse;
import com.veyra.rentacar.features.rentals.entities.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentalMapper {

    Rental toEntity(CreateRentalRequest request);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "status", expression = "java(rental.getStatus().name())")
    @Mapping(target = "carBrandName", source = "car.brand.name")
    @Mapping(target = "carModelName", source = "car.model.name")
    @Mapping(target = "carImageUrl",
            expression = "java(rental.getCar().getImageUrls().isEmpty() ? null : rental.getCar().getImageUrls().get(0))")
    GetRentalResponse toResponse(Rental rental);

    List<GetRentalResponse> toResponseList(List<Rental> rentals);
}
