package com.veyra.rentacar.features.auth.mapper;

import com.veyra.rentacar.features.auth.dto.request.RegisterRequest;
import com.veyra.rentacar.features.auth.dto.response.UserResponse;
import com.veyra.rentacar.features.auth.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AuthMapper {

    User toEntity(RegisterRequest request);

    UserResponse toResponse(User user);
}
