package com.veyra.rentacar.features.auth.abstracts;

import com.veyra.rentacar.features.auth.dtos.requests.LoginRequest;
import com.veyra.rentacar.features.auth.dtos.requests.RegisterRequest;
import com.veyra.rentacar.features.auth.dtos.responses.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
