package com.veyra.rentacar.features.dashboard.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusDistributionResponse {
    private String status;
    private String label;
    private int count;
    private double percentage;
}
