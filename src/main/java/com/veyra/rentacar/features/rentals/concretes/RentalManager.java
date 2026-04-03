package com.veyra.rentacar.features.rentals.concretes;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.cars.entities.Car;
import com.veyra.rentacar.features.cars.repositories.CarRepository;
import com.veyra.rentacar.features.cars.rules.CarBusinessRules;
import com.veyra.rentacar.features.rentals.abstracts.RentalService;
import com.veyra.rentacar.features.rentals.dtos.requests.CreateRentalRequest;
import com.veyra.rentacar.features.rentals.dtos.requests.UpdateRentalStatusRequest;
import com.veyra.rentacar.features.rentals.dtos.responses.GetRentalResponse;
import com.veyra.rentacar.features.rentals.dtos.responses.RentalStatsResponse;
import com.veyra.rentacar.features.rentals.entities.Rental;
import com.veyra.rentacar.features.rentals.entities.ReservationStatus;
import com.veyra.rentacar.features.rentals.mappers.RentalMapper;
import com.veyra.rentacar.features.rentals.repositories.RentalRepository;
import com.veyra.rentacar.features.rentals.rules.RentalBusinessRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalManager implements RentalService {

    private final RentalRepository rentalRepository;
    private final RentalBusinessRules rentalBusinessRules;
    private final CarBusinessRules carBusinessRules;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GetRentalResponse> getAll() {
        return rentalMapper.toResponseList(rentalRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public GetRentalResponse getById(String id) {
        return rentalRepository.findById(id)
                .map(rentalMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Kiralama bulunamadı: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetRentalResponse> getByUserId(String userId) {
        return rentalMapper.toResponseList(rentalRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetRentalResponse> getByStatus(ReservationStatus status) {
        return rentalMapper.toResponseList(rentalRepository.findByStatus(status));
    }

    @Override
    @Transactional
    public GetRentalResponse create(CreateRentalRequest request) {
        carBusinessRules.checkIfCarExists(request.getCarId());
        carBusinessRules.checkIfCarIsAvailable(request.getCarId());
        rentalBusinessRules.checkIfCarAlreadyRented(request.getCarId());

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new BusinessException("Araç bulunamadı: " + request.getCarId()));

        Rental rental = rentalMapper.toEntity(request);
        rental.setCar(car);
        // reservationCode ve createdAt @PrePersist'te otomatik set edilir

        return rentalMapper.toResponse(rentalRepository.save(rental));
    }

    @Override
    @Transactional
    public GetRentalResponse updateStatus(String id, UpdateRentalStatusRequest request) {
        rentalBusinessRules.checkIfRentalExists(id);

        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Kiralama bulunamadı: " + id));

        rental.setStatus(request.getStatus());

        return rentalMapper.toResponse(rentalRepository.save(rental));
    }

    @Override
    @Transactional(readOnly = true)
    public RentalStatsResponse getStats() {
        List<Rental> all = rentalRepository.findAll();

        int total = all.size();
        int pending   = (int) all.stream().filter(r -> r.getStatus() == ReservationStatus.PENDING).count();
        int confirmed = (int) all.stream().filter(r -> r.getStatus() == ReservationStatus.CONFIRMED).count();
        int active    = (int) all.stream().filter(r -> r.getStatus() == ReservationStatus.ACTIVE).count();
        int completed = (int) all.stream().filter(r -> r.getStatus() == ReservationStatus.COMPLETED).count();
        int cancelled = (int) all.stream().filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count();

        BigDecimal totalRevenue = rentalRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        return new RentalStatsResponse(total, pending, confirmed, active, completed, cancelled, totalRevenue);
    }
}
