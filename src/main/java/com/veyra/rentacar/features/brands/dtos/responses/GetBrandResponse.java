package com.veyra.rentacar.features.brands.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetBrandResponse {

    private String id;

    private String name;

    private String slug;

    private String logoUrl;
}
