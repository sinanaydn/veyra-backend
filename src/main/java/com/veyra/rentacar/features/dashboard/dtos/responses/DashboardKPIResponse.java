package com.veyra.rentacar.features.dashboard.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKPIResponse {
    private int totalReservations;
    private int activeReservations;
    private int pendingReservations;
    private BigDecimal totalRevenue;
    private int totalCars;
    private int totalUsers;
}
