package com.veyra.rentacar.features.dashboard.controllers;

import com.veyra.rentacar.features.dashboard.abstracts.DashboardService;
import com.veyra.rentacar.features.dashboard.dtos.responses.*;
import com.veyra.rentacar.features.rentals.dtos.responses.GetRentalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Admin Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpis")
    public ResponseEntity<DashboardKPIResponse> getKPIs() {
        return ResponseEntity.ok(dashboardService.getKPIs());
    }

    @GetMapping("/status-distribution")
    public ResponseEntity<List<StatusDistributionResponse>> getStatusDistribution() {
        return ResponseEntity.ok(dashboardService.getStatusDistribution());
    }

    @GetMapping("/fleet-availability")
    public ResponseEntity<List<FleetAvailabilityResponse>> getFleetAvailability() {
        return ResponseEntity.ok(dashboardService.getFleetAvailability());
    }

    @GetMapping("/city-performance")
    public ResponseEntity<List<CityPerformanceResponse>> getCityPerformance() {
        return ResponseEntity.ok(dashboardService.getCityPerformance());
    }

    @GetMapping("/recent-reservations")
    public ResponseEntity<List<GetRentalResponse>> getRecentReservations() {
        return ResponseEntity.ok(dashboardService.getRecentReservations());
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<RecentActivityResponse>> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }
}
