package com.veyra.rentacar.features.models.controllers;

import com.veyra.rentacar.features.models.abstracts.CarModelService;
import com.veyra.rentacar.features.models.dtos.requests.CreateCarModelRequest;
import com.veyra.rentacar.features.models.dtos.requests.UpdateCarModelRequest;
import com.veyra.rentacar.features.models.dtos.responses.GetCarModelResponse;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carmodels")
@Tag(name = "Car Models", description = "Araç Modeli Yönetimi")
public class CarModelsController {

    private final CarModelService carModelService;

    @GetMapping
    public ResponseEntity<List<GetCarModelResponse>> getAll() {
        return ResponseEntity.ok(carModelService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetCarModelResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(carModelService.getById(id));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<GetCarModelResponse>> getByBrandId(@PathVariable String brandId) {
        return ResponseEntity.ok(carModelService.getByBrandId(brandId));
    }

    @PostMapping
    public ResponseEntity<GetCarModelResponse> create(@RequestBody @Valid CreateCarModelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carModelService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetCarModelResponse> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateCarModelRequest request) {
        return ResponseEntity.ok(carModelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        carModelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
