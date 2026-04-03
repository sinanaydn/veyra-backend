package com.veyra.rentacar.features.rentals.controllers;

import com.veyra.rentacar.features.rentals.abstracts.RentalService;
import com.veyra.rentacar.features.rentals.dtos.requests.CreateRentalRequest;
import com.veyra.rentacar.features.rentals.dtos.requests.UpdateRentalStatusRequest;
import com.veyra.rentacar.features.rentals.dtos.responses.GetRentalResponse;
import com.veyra.rentacar.features.rentals.dtos.responses.RentalStatsResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rentals")
@Tag(name = "Rentals", description = "Rezervasyon Yönetimi")
public class RentalsController {

    private final RentalService rentalService;

    @GetMapping
    public ResponseEntity<List<GetRentalResponse>> getAll() {
        return ResponseEntity.ok(rentalService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetRentalResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(rentalService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GetRentalResponse>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(rentalService.getByUserId(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<RentalStatsResponse> getStats() {
        return ResponseEntity.ok(rentalService.getStats());
    }

    @PostMapping
    public ResponseEntity<GetRentalResponse> create(@RequestBody @Valid CreateRentalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.create(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<GetRentalResponse> updateStatus(
            @PathVariable String id,
            @RequestBody @Valid UpdateRentalStatusRequest request) {
        return ResponseEntity.ok(rentalService.updateStatus(id, request));
    }
}
