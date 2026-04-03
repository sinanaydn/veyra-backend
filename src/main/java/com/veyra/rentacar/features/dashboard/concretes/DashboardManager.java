package com.veyra.rentacar.features.dashboard.concretes;

import com.veyra.rentacar.features.auth.entities.User;
import com.veyra.rentacar.features.auth.repositories.UserRepository;
import com.veyra.rentacar.features.cars.entities.Car;
import com.veyra.rentacar.features.cars.entities.enums.AvailabilityStatus;
import com.veyra.rentacar.features.cars.repositories.CarRepository;
import com.veyra.rentacar.features.dashboard.abstracts.DashboardService;
import com.veyra.rentacar.features.dashboard.dtos.responses.*;
import com.veyra.rentacar.features.rentals.dtos.responses.GetRentalResponse;
import com.veyra.rentacar.features.rentals.entities.Rental;
import com.veyra.rentacar.features.rentals.entities.ReservationStatus;
import com.veyra.rentacar.features.rentals.mappers.RentalMapper;
import com.veyra.rentacar.features.rentals.repositories.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardManager implements DashboardService {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RentalMapper rentalMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardKPIResponse getKPIs() {
        int totalReservations = (int) rentalRepository.count();
        int activeReservations = rentalRepository.findByStatus(ReservationStatus.ACTIVE).size();
        int pendingReservations = rentalRepository.findByStatus(ReservationStatus.PENDING).size();
        BigDecimal totalRevenue = Optional.ofNullable(rentalRepository.getTotalRevenue())
                .orElse(BigDecimal.ZERO);
        int totalCars = (int) carRepository.count();
        int totalUsers = (int) userRepository.count();

        return new DashboardKPIResponse(
                totalReservations, activeReservations, pendingReservations,
                totalRevenue, totalCars, totalUsers
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatusDistributionResponse> getStatusDistribution() {
        long total = rentalRepository.count();

        Map<String, String> labels = Map.of(
                "PENDING", "Beklemede",
                "CONFIRMED", "Onaylandı",
                "ACTIVE", "Aktif",
                "COMPLETED", "Tamamlandı",
                "CANCELLED", "İptal"
        );

        return Arrays.stream(ReservationStatus.values())
                .map(status -> {
                    int count = rentalRepository.findByStatus(status).size();
                    double percentage = total == 0 ? 0.0 : (double) count / total * 100;
                    return new StatusDistributionResponse(status.name(), labels.get(status.name()), count, percentage);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FleetAvailabilityResponse> getFleetAvailability() {
        Map<String, String> labels = Map.of(
                "AVAILABLE", "Müsait",
                "RESERVED", "Rezerveli",
                "MAINTENANCE", "Bakımda"
        );

        return Arrays.stream(AvailabilityStatus.values())
                .map(status -> {
                    int count = carRepository.findByAvailability(status).size();
                    return new FleetAvailabilityResponse(status.name(), labels.get(status.name()), count);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityPerformanceResponse> getCityPerformance() {
        List<Car> allCars = carRepository.findAll();

        Map<String, Integer> carCountByCity = allCars.stream()
                .filter(c -> c.getCity() != null)
                .collect(Collectors.groupingBy(Car::getCity, Collectors.summingInt(c -> 1)));

        Map<String, String> carCityMap = allCars.stream()
                .filter(c -> c.getCity() != null)
                .collect(Collectors.toMap(Car::getId, Car::getCity, (a, b) -> a));

        Map<String, Integer> rentalCountByCity = rentalRepository.findAll().stream()
                .filter(r -> carCityMap.containsKey(r.getCar().getId()))
                .collect(Collectors.groupingBy(
                        r -> carCityMap.get(r.getCar().getId()),
                        Collectors.summingInt(r -> 1)
                ));

        return carCountByCity.entrySet().stream()
                .map(entry -> new CityPerformanceResponse(
                        entry.getKey(),
                        entry.getValue(),
                        rentalCountByCity.getOrDefault(entry.getKey(), 0)
                ))
                .sorted(Comparator.comparingInt(CityPerformanceResponse::getReservationCount).reversed())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetRentalResponse> getRecentReservations() {
        List<Rental> recent = rentalRepository.findAll().stream()
                .sorted(Comparator.comparing(Rental::getCreatedAt).reversed())
                .limit(5)
                .toList();
        return rentalMapper.toResponseList(recent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentActivityResponse> getRecentActivity() {
        List<RecentActivityResponse> rentalActivities = rentalRepository.findAll().stream()
                .sorted(Comparator.comparing(Rental::getCreatedAt).reversed())
                .limit(5)
                .map(r -> new RecentActivityResponse(
                        r.getId(),
                        "reservation",
                        "Yeni Rezervasyon",
                        r.getReservationCode() + " · " + r.getPickupLocation(),
                        r.getCreatedAt()
                ))
                .toList();

        List<RecentActivityResponse> userActivities = userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .limit(5)
                .map(u -> new RecentActivityResponse(
                        u.getId(),
                        "user",
                        "Yeni Kullanıcı",
                        u.getFirstName() + " " + u.getLastName() + " · " + u.getEmail(),
                        u.getCreatedAt()
                ))
                .toList();

        return Stream.concat(rentalActivities.stream(), userActivities.stream())
                .sorted(Comparator.comparing(RecentActivityResponse::getTimestamp).reversed())
                .limit(10)
                .toList();
    }
}
