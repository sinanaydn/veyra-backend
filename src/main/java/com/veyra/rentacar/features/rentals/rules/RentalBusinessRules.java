package com.veyra.rentacar.features.rentals.rules;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.rentals.entities.ReservationStatus;
import com.veyra.rentacar.features.rentals.repositories.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RentalBusinessRules {

    private final RentalRepository rentalRepository;

    public void checkIfRentalExists(String id) {
        if (!rentalRepository.existsById(id)) {
            throw new BusinessException("Kiralama bulunamadı: " + id);
        }
    }

    public void checkIfCarAlreadyRented(String carId) {
        List<ReservationStatus> activeStatuses = List.of(
                ReservationStatus.PENDING,
                ReservationStatus.CONFIRMED,
                ReservationStatus.ACTIVE
        );
        if (rentalRepository.existsByCarIdAndStatusIn(carId, activeStatuses)) {
            throw new BusinessException("Bu araç için aktif bir kiralama kaydı zaten mevcut: " + carId);
        }
    }
}
