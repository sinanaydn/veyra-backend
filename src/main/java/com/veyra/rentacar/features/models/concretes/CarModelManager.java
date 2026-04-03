package com.veyra.rentacar.features.models.concretes;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.brands.rules.BrandBusinessRules;
import com.veyra.rentacar.features.models.abstracts.CarModelService;
import com.veyra.rentacar.features.models.dtos.requests.CreateCarModelRequest;
import com.veyra.rentacar.features.models.dtos.requests.UpdateCarModelRequest;
import com.veyra.rentacar.features.models.dtos.responses.GetCarModelResponse;
import com.veyra.rentacar.features.models.entities.CarModel;
import com.veyra.rentacar.features.models.mappers.CarModelMapper;
import com.veyra.rentacar.features.models.repositories.CarModelRepository;
import com.veyra.rentacar.features.models.rules.CarModelBusinessRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarModelManager implements CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarModelBusinessRules carModelBusinessRules;
    private final CarModelMapper carModelMapper;
    private final BrandBusinessRules brandBusinessRules;

    @Override
    @Transactional(readOnly = true)
    public List<GetCarModelResponse> getAll() {
        return carModelMapper.toResponseList(carModelRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetCarModelResponse> getByBrandId(String brandId) {
        return carModelMapper.toResponseList(carModelRepository.findByBrandId(brandId));
    }

    @Override
    @Transactional(readOnly = true)
    public GetCarModelResponse getById(String id) {
        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Model bulunamadı: " + id));
        return carModelMapper.toResponse(carModel);
    }

    @Override
    @Transactional
    public GetCarModelResponse create(CreateCarModelRequest request) {
        brandBusinessRules.checkIfBrandExists(request.getBrandId());
        carModelBusinessRules.checkIfModelNameUniqueInBrand(request.getName(), request.getBrandId());

        CarModel carModel = carModelMapper.toEntity(request);
        return carModelMapper.toResponse(carModelRepository.save(carModel));
    }

    @Override
    @Transactional
    public GetCarModelResponse update(String id, UpdateCarModelRequest request) {
        carModelBusinessRules.checkIfModelExists(id);

        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Model bulunamadı: " + id));

        if (request.getName() != null && !request.getName().equals(carModel.getName())) {
            carModelBusinessRules.checkIfModelNameUniqueInBrand(request.getName(), carModel.getBrand().getId());
        }

        carModelMapper.updateModelFromRequest(request, carModel);
        return carModelMapper.toResponse(carModelRepository.save(carModel));
    }

    @Override
    @Transactional
    public void delete(String id) {
        carModelBusinessRules.checkIfModelExists(id);
        carModelRepository.deleteById(id);
    }
}
