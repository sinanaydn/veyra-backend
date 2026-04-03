package com.veyra.rentacar.features.auth.concretes;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.core.security.CustomUserDetails;
import com.veyra.rentacar.core.security.JwtService;
import com.veyra.rentacar.features.auth.abstracts.AuthService;
import com.veyra.rentacar.features.auth.dtos.requests.LoginRequest;
import com.veyra.rentacar.features.auth.dtos.requests.RegisterRequest;
import com.veyra.rentacar.features.auth.dtos.responses.AuthResponse;
import com.veyra.rentacar.features.auth.entities.User;
import com.veyra.rentacar.features.auth.entities.UserRole;
import com.veyra.rentacar.features.auth.mappers.AuthMapper;
import com.veyra.rentacar.features.auth.repositories.UserRepository;
import com.veyra.rentacar.features.auth.rules.AuthBusinessRules;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthManager implements AuthService {

    private final UserRepository userRepository;
    private final AuthBusinessRules authBusinessRules;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        authBusinessRules.checkIfEmailIsUnique(request.getEmail());

        User user = authMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(authMapper.toResponse(savedUser), token);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(authMapper.toResponse(user), token);
    }
}
