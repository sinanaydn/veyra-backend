package com.veyra.rentacar.features.cars.rules;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.cars.entities.enums.AvailabilityStatus;
import com.veyra.rentacar.features.cars.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarBusinessRules {

    private final CarRepository carRepository;

    public void checkIfCarExists(String id) {
        if (!carRepository.existsById(id)) {
            throw new BusinessException("Araç bulunamadı: " + id);
        }
    }

    public void checkIfSlugUnique(String slug) {
        if (carRepository.existsBySlug(slug)) {
            throw new BusinessException("Bu slug zaten kullanımda: " + slug);
        }
    }

    public void checkIfCarIsAvailable(String id) {
        carRepository.findById(id)
                .filter(car -> car.getAvailability() == AvailabilityStatus.AVAILABLE)
                .orElseThrow(() -> new BusinessException("Araç şu an müsait değil: " + id));
    }
}
