package com.veyra.rentacar.features.users.abstracts;

import com.veyra.rentacar.features.auth.dtos.responses.UserResponse;
import com.veyra.rentacar.features.users.dtos.responses.AdminUserResponse;

import java.util.List;

public interface UserService {
    UserResponse getMe(String email);
    List<AdminUserResponse> getAllWithStats();
    AdminUserResponse getByIdWithStats(String id);
}
