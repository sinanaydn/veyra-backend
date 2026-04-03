package com.veyra.rentacar.features.brands.abstracts;

import com.veyra.rentacar.features.brands.dtos.requests.CreateBrandRequest;
import com.veyra.rentacar.features.brands.dtos.requests.UpdateBrandRequest;
import com.veyra.rentacar.features.brands.dtos.responses.GetBrandResponse;

import java.util.List;

public interface BrandService {

    List<GetBrandResponse> getAll();

    GetBrandResponse getById(String id);

    GetBrandResponse getBySlug(String slug);

    GetBrandResponse create(CreateBrandRequest request);

    GetBrandResponse update(String id, UpdateBrandRequest request);

    void delete(String id);
}
