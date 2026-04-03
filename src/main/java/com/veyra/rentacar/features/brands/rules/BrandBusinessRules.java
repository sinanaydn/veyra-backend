package com.veyra.rentacar.features.brands.rules;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.brands.repositories.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrandBusinessRules {

    private final BrandRepository brandRepository;

    public void checkIfBrandExists(String id) {
        if (!brandRepository.existsById(id)) {
            throw new BusinessException("Marka bulunamadı: " + id);
        }
    }

    public void checkIfBrandNameUnique(String name) {
        if (brandRepository.existsByName(name)) {
            throw new BusinessException("Bu marka adı zaten kullanımda: " + name);
        }
    }

    public void checkIfBrandSlugUnique(String slug) {
        if (brandRepository.existsBySlug(slug)) {
            throw new BusinessException("Bu slug zaten kullanımda: " + slug);
        }
    }
}
