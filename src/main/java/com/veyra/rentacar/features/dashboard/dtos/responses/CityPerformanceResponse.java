package com.veyra.rentacar.features.dashboard.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityPerformanceResponse {
    private String city;
    private int carCount;
    private int reservationCount;
}
