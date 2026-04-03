package com.veyra.rentacar.features.brands.controllers;

import com.veyra.rentacar.features.brands.abstracts.BrandService;
import com.veyra.rentacar.features.brands.dtos.requests.CreateBrandRequest;
import com.veyra.rentacar.features.brands.dtos.requests.UpdateBrandRequest;
import com.veyra.rentacar.features.brands.dtos.responses.GetBrandResponse;
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
@RequestMapping("/api/v1/brands")
@Tag(name = "Brands", description = "Marka Yönetimi")
public class BrandsController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<GetBrandResponse>> getAll() {
        return ResponseEntity.ok(brandService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetBrandResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(brandService.getById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<GetBrandResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(brandService.getBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<GetBrandResponse> create(@RequestBody @Valid CreateBrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetBrandResponse> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateBrandRequest request) {
        return ResponseEntity.ok(brandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
