package com.veyra.rentacar.features.dashboard.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FleetAvailabilityResponse {
    private String status;
    private String label;
    private int count;
}
