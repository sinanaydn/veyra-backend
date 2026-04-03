package com.veyra.rentacar.features.dashboard.abstracts;

import com.veyra.rentacar.features.dashboard.dtos.responses.*;
import com.veyra.rentacar.features.rentals.dtos.responses.GetRentalResponse;

import java.util.List;

public interface DashboardService {
    DashboardKPIResponse getKPIs();
    List<StatusDistributionResponse> getStatusDistribution();
    List<FleetAvailabilityResponse> getFleetAvailability();
    List<CityPerformanceResponse> getCityPerformance();
    List<GetRentalResponse> getRecentReservations();
    List<RecentActivityResponse> getRecentActivity();
}
