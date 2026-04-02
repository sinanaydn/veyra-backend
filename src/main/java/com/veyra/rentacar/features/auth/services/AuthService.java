package com.veyra.rentacar.features.auth.services;

import com.veyra.rentacar.features.auth.dto.request.LoginRequest;
import com.veyra.rentacar.features.auth.dto.request.RegisterRequest;
import com.veyra.rentacar.features.auth.dto.response.AuthResponse;

public interface AuthService {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse login(LoginRequest request);
}
