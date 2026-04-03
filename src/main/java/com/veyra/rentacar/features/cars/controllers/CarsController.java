package com.veyra.rentacar.features.cars.controllers;

import com.veyra.rentacar.features.cars.abstracts.CarService;
import com.veyra.rentacar.features.cars.dtos.requests.CreateCarRequest;
import com.veyra.rentacar.features.cars.dtos.requests.UpdateCarRequest;
import com.veyra.rentacar.features.cars.dtos.responses.GetCarResponse;
import com.veyra.rentacar.features.cars.entities.enums.AvailabilityStatus;
import com.veyra.rentacar.features.cars.entities.enums.CarSortOption;
import com.veyra.rentacar.features.cars.entities.enums.FuelType;
import com.veyra.rentacar.features.cars.entities.enums.Transmission;
import com.veyra.rentacar.features.models.entities.CarCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
@Tag(name = "Cars", description = "Araç Yönetimi")
public class CarsController {

    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<GetCarResponse>> getAll(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String brandId,
            @RequestParam(required = false) String modelId,
            @RequestParam(required = false) Transmission transmission,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) CarCategory category,
            @RequestParam(required = false) Integer minSeats,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean airportEligible,
            @RequestParam(required = false) AvailabilityStatus availability,
            @RequestParam(required = false, defaultValue = "RECOMMENDED") CarSortOption sort
    ) {
        return ResponseEntity.ok(carService.getAll(
                city, brandId, modelId, transmission, fuelType,
                category, minSeats, minPrice, maxPrice, airportEligible, availability, sort
        ));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<GetCarResponse>> getFeatured(
            @RequestParam(defaultValue = "4") int limit
    ) {
        return ResponseEntity.ok(carService.getFeatured(limit));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<GetCarResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(carService.getBySlug(slug));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<GetCarResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(carService.getById(id));
    }

    @PostMapping
    public ResponseEntity<GetCarResponse> create(@RequestBody @Valid CreateCarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetCarResponse> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateCarRequest request
    ) {
        return ResponseEntity.ok(carService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
