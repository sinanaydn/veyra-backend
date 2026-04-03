package com.veyra.rentacar.features.models.abstracts;

import com.veyra.rentacar.features.models.dtos.requests.CreateCarModelRequest;
import com.veyra.rentacar.features.models.dtos.requests.UpdateCarModelRequest;
import com.veyra.rentacar.features.models.dtos.responses.GetCarModelResponse;

import java.util.List;

public interface CarModelService {

    List<GetCarModelResponse> getAll();

    List<GetCarModelResponse> getByBrandId(String brandId);

    GetCarModelResponse getById(String id);

    GetCarModelResponse create(CreateCarModelRequest request);

    GetCarModelResponse update(String id, UpdateCarModelRequest request);

    void delete(String id);
}
