package com.veyra.rentacar.features.rentals.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalStatsResponse {

    private int total;
    private int pending;
    private int confirmed;
    private int active;
    private int completed;
    private int cancelled;
    private BigDecimal totalRevenue;
}
