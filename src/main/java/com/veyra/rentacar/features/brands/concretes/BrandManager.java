package com.veyra.rentacar.features.brands.concretes;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.brands.abstracts.BrandService;
import com.veyra.rentacar.features.brands.dtos.requests.CreateBrandRequest;
import com.veyra.rentacar.features.brands.dtos.requests.UpdateBrandRequest;
import com.veyra.rentacar.features.brands.dtos.responses.GetBrandResponse;
import com.veyra.rentacar.features.brands.entities.Brand;
import com.veyra.rentacar.features.brands.mappers.BrandMapper;
import com.veyra.rentacar.features.brands.repositories.BrandRepository;
import com.veyra.rentacar.features.brands.rules.BrandBusinessRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandManager implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandBusinessRules brandBusinessRules;
    private final BrandMapper brandMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GetBrandResponse> getAll() {
        return brandMapper.toResponseList(brandRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public GetBrandResponse getById(String id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Marka bulunamadı: " + id));
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public GetBrandResponse getBySlug(String slug) {
        return brandRepository.findBySlug(slug)
                .map(brandMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Marka bulunamadı: " + slug));
    }

    @Override
    @Transactional
    public GetBrandResponse create(CreateBrandRequest request) {
        brandBusinessRules.checkIfBrandNameUnique(request.getName());

        Brand brand = brandMapper.toEntity(request);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public GetBrandResponse update(String id, UpdateBrandRequest request) {
        brandBusinessRules.checkIfBrandExists(id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Marka bulunamadı: " + id));

        if (request.getName() != null && !request.getName().equals(brand.getName())) {
            brandBusinessRules.checkIfBrandNameUnique(request.getName());
        }

        brandMapper.updateBrandFromRequest(request, brand);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void delete(String id) {
        brandBusinessRules.checkIfBrandExists(id);
        brandRepository.deleteById(id);
    }
}
