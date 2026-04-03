package com.veyra.rentacar.features.cars.dtos.requests;

import com.veyra.rentacar.features.cars.entities.enums.FuelPolicy;
import com.veyra.rentacar.features.cars.entities.enums.FuelType;
import com.veyra.rentacar.features.cars.entities.enums.Transmission;
import com.veyra.rentacar.features.models.entities.CarCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarRequest {

    @NotBlank(message = "Marka ID boş olamaz")
    private String brandId;

    @NotBlank(message = "Model ID boş olamaz")
    private String modelId;

    @NotNull(message = "Yıl boş olamaz")
    private Integer year;

    @NotNull(message = "Kategori boş olamaz")
    private CarCategory category;

    @NotBlank(message = "Şehir boş olamaz")
    private String city;

    private List<String> pickupLocations;

    private List<String> returnLocations;

    @NotNull(message = "Vites tipi boş olamaz")
    private Transmission transmission;

    @NotNull(message = "Yakıt tipi boş olamaz")
    private FuelType fuelType;

    private Integer seats;

    private Integer baggage;

    private Integer doors;

    private List<String> imageUrls;

    @NotNull(message = "Günlük fiyat boş olamaz")
    private BigDecimal pricePerDay;

    private BigDecimal depositAmount;

    private Integer mileageLimit;

    private FuelPolicy fuelPolicy;

    private Boolean airportEligible;

    private String description;

    private List<String> features;
}
