package com.veyra.rentacar.features.models.rules;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.models.repositories.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarModelBusinessRules {

    private final CarModelRepository carModelRepository;

    public void checkIfModelExists(String id) {
        if (!carModelRepository.existsById(id)) {
            throw new BusinessException("Model bulunamadı: " + id);
        }
    }

    public void checkIfModelNameUniqueInBrand(String name, String brandId) {
        if (carModelRepository.existsByNameAndBrandId(name, brandId)) {
            throw new BusinessException("Bu markada aynı isimde model zaten mevcut: " + name);
        }
    }
}
