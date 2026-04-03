package com.veyra.rentacar.features.cars.dtos.responses;

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
public class InsurancePackageResponse {

    private String id;

    private String name;

    private String description;

    private BigDecimal pricePerDay;

    private List<String> coverageItems;
}
