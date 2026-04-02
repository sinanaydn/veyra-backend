package com.veyra.rentacar.features.auth.rules;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthBusinessRules {

    private final UserRepository userRepository;

    public void checkIfEmailIsUnique(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Bu e-posta adresi zaten kullanımda: " + email);
        }
    }
}
