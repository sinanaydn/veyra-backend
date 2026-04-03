package com.veyra.rentacar.features.cars.abstracts;

import com.veyra.rentacar.features.cars.dtos.requests.CreateCarRequest;
import com.veyra.rentacar.features.cars.dtos.requests.UpdateCarRequest;
import com.veyra.rentacar.features.cars.dtos.responses.GetCarResponse;
import com.veyra.rentacar.features.cars.entities.enums.AvailabilityStatus;
import com.veyra.rentacar.features.cars.entities.enums.CarSortOption;
import com.veyra.rentacar.features.cars.entities.enums.FuelType;
import com.veyra.rentacar.features.cars.entities.enums.Transmission;
import com.veyra.rentacar.features.models.entities.CarCategory;

import java.math.BigDecimal;
import java.util.List;

public interface CarService {

    List<GetCarResponse> getAll(
            String city,
            String brandId,
            String modelId,
            Transmission transmission,
            FuelType fuelType,
            CarCategory category,
            Integer minSeats,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean airportEligible,
            AvailabilityStatus availability,
            CarSortOption sort
    );

    GetCarResponse getBySlug(String slug);

    GetCarResponse getById(String id);

    List<GetCarResponse> getFeatured(int limit);

    List<GetCarResponse> getSimilar(String carId, int limit);

    GetCarResponse create(CreateCarRequest request);

    GetCarResponse update(String id, UpdateCarRequest request);

    void delete(String id);
}
