package com.veyra.rentacar.features.cars.dtos.requests;

import com.veyra.rentacar.features.cars.entities.enums.AvailabilityStatus;
import com.veyra.rentacar.features.cars.entities.enums.FuelPolicy;
import com.veyra.rentacar.features.cars.entities.enums.FuelType;
import com.veyra.rentacar.features.cars.entities.enums.Transmission;
import com.veyra.rentacar.features.models.entities.CarCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCarRequest {

    private String brandId;

    private String modelId;

    private Integer year;

    private CarCategory category;

    private String city;

    private List<String> pickupLocations;

    private List<String> returnLocations;

    private Transmission transmission;

    private FuelType fuelType;

    private Integer seats;

    private Integer baggage;

    private Integer doors;

    private List<String> imageUrls;

    private BigDecimal pricePerDay;

    private BigDecimal depositAmount;

    private Integer mileageLimit;

    private FuelPolicy fuelPolicy;

    private AvailabilityStatus availability;

    private Boolean airportEligible;

    private String description;

    private List<String> features;
}
