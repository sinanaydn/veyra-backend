package com.veyra.rentacar.features.brands.mappers;

import com.veyra.rentacar.features.brands.dtos.requests.CreateBrandRequest;
import com.veyra.rentacar.features.brands.dtos.requests.UpdateBrandRequest;
import com.veyra.rentacar.features.brands.dtos.responses.GetBrandResponse;
import com.veyra.rentacar.features.brands.entities.Brand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {

    Brand toEntity(CreateBrandRequest request);

    GetBrandResponse toResponse(Brand brand);

    List<GetBrandResponse> toResponseList(List<Brand> brands);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBrandFromRequest(UpdateBrandRequest request, @MappingTarget Brand brand);
}
