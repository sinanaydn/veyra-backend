package com.veyra.rentacar.features.models.dtos.requests;

import com.veyra.rentacar.features.models.entities.CarCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarModelRequest {

    @NotBlank(message = "Marka ID boş olamaz")
    private String brandId;

    @NotBlank(message = "Model adı boş olamaz")
    private String name;

    @NotNull(message = "Kategori boş olamaz")
    private CarCategory category;
}
