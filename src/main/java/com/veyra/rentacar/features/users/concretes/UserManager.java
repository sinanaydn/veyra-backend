package com.veyra.rentacar.features.users.concretes;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.auth.dtos.responses.UserResponse;
import com.veyra.rentacar.features.auth.entities.User;
import com.veyra.rentacar.features.auth.mappers.AuthMapper;
import com.veyra.rentacar.features.auth.repositories.UserRepository;
import com.veyra.rentacar.features.rentals.entities.Rental;
import com.veyra.rentacar.features.rentals.repositories.RentalRepository;
import com.veyra.rentacar.features.users.abstracts.UserService;
import com.veyra.rentacar.features.users.dtos.responses.AdminUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManager implements UserService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final AuthMapper authMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı: " + email));
        return authMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllWithStats() {
        return userRepository.findAll().stream()
                .map(this::toAdminUserResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getByIdWithStats(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Kullanıcı bulunamadı: " + id));
        return toAdminUserResponse(user);
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        List<Rental> rentals = rentalRepository.findByUserId(user.getId());
        BigDecimal totalSpent = rentals.stream()
                .map(Rental::getGrandTotal)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AdminUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                rentals.size(),
                totalSpent
        );
    }
}
