package com.veyra.rentacar.features.auth.mappers;

import com.veyra.rentacar.features.auth.dtos.requests.RegisterRequest;
import com.veyra.rentacar.features.auth.dtos.responses.UserResponse;
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
