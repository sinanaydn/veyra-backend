package com.veyra.rentacar.features.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCarModelResponse {

    private String id;

    private String name;

    private String brandName;

    private String category;
}
