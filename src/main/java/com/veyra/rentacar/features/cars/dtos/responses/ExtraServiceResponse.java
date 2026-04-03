package com.veyra.rentacar.features.cars.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtraServiceResponse {

    private String id;

    private String name;

    private String description;

    private BigDecimal pricePerDay;

    private String priceType;
}
