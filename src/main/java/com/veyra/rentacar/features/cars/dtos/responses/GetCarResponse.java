package com.veyra.rentacar.features.cars.dtos.responses;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCarResponse {

    private String id;

    private String slug;

    private String brandName;

    private String modelName;

    private Integer year;

    private String category;

    private String city;

    private List<String> pickupLocations;

    private List<String> returnLocations;

    private String transmission;

    private String fuelType;

    private Integer seats;

    private Integer baggage;

    private Integer doors;

    private List<String> imageUrls;

    private BigDecimal pricePerDay;

    private BigDecimal depositAmount;

    private Integer mileageLimit;

    private String fuelPolicy;

    private String availability;

    private Boolean airportEligible;

    private String description;

    private List<String> features;

    private List<InsurancePackageResponse> insurancePackages;

    private List<ExtraServiceResponse> extras;

    private Double rating;

    private Integer reviewCount;
}
