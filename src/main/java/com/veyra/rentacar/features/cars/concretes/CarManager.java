package com.veyra.rentacar.features.cars.concretes;

import com.veyra.rentacar.core.exception.BusinessException;
import com.veyra.rentacar.features.brands.rules.BrandBusinessRules;
import com.veyra.rentacar.features.cars.abstracts.CarService;
import com.veyra.rentacar.features.cars.dtos.requests.CreateCarRequest;
import com.veyra.rentacar.features.cars.dtos.requests.UpdateCarRequest;
import com.veyra.rentacar.features.cars.dtos.responses.GetCarResponse;
import com.veyra.rentacar.features.cars.entities.Car;
import com.veyra.rentacar.features.cars.entities.enums.AvailabilityStatus;
import com.veyra.rentacar.features.cars.entities.enums.CarSortOption;
import com.veyra.rentacar.features.cars.entities.enums.FuelType;
import com.veyra.rentacar.features.cars.entities.enums.Transmission;
import com.veyra.rentacar.features.cars.mappers.CarMapper;
import com.veyra.rentacar.features.cars.repositories.CarRepository;
import com.veyra.rentacar.features.cars.rules.CarBusinessRules;
import com.veyra.rentacar.features.models.entities.CarCategory;
import com.veyra.rentacar.features.models.rules.CarModelBusinessRules;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarManager implements CarService {

    private final CarRepository carRepository;
    private final CarBusinessRules carBusinessRules;
    private final BrandBusinessRules brandBusinessRules;
    private final CarModelBusinessRules carModelBusinessRules;
    private final CarMapper carMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GetCarResponse> getAll(
            String city,
            String brandId,
            String modelId,
            Transmission transmission,
            FuelType fuelType,
            CarCategory category,
            Integer minSeats,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean airportEligible,
            AvailabilityStatus availability,
            CarSortOption sort
    ) {
        List<Car> cars = carRepository.findByFilters(
                city, brandId, modelId, transmission, fuelType,
                category, minSeats, minPrice, maxPrice, airportEligible
        );

        if (availability != null) {
            cars = cars.stream()
                    .filter(c -> c.getAvailability() == availability)
                    .toList();
        }

        cars = sorted(cars, sort);

        return carMapper.toResponseList(cars);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCarResponse getBySlug(String slug) {
        return carRepository.findBySlug(slug)
                .map(carMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Araç bulunamadı: " + slug));
    }

    @Override
    @Transactional(readOnly = true)
    public GetCarResponse getById(String id) {
        return carRepository.findById(id)
                .map(carMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Araç bulunamadı: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetCarResponse> getFeatured(int limit) {
        return carMapper.toResponseList(
                carRepository.findTopByAvailabilityOrderByRatingDesc(
                        AvailabilityStatus.AVAILABLE,
                        PageRequest.of(0, limit)
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetCarResponse> getSimilar(String carId, int limit) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new BusinessException("Araç bulunamadı: " + carId));

        return carMapper.toResponseList(
                carRepository.findSimilarCars(
                        car.getCategory(),
                        carId,
                        PageRequest.of(0, limit)
                )
        );
    }

    @Override
    @Transactional
    public GetCarResponse create(CreateCarRequest request) {
        brandBusinessRules.checkIfBrandExists(request.getBrandId());
        carModelBusinessRules.checkIfModelExists(request.getModelId());

        Car car = carMapper.toEntity(request);
        return carMapper.toResponse(carRepository.save(car));
    }

    @Override
    @Transactional
    public GetCarResponse update(String id, UpdateCarRequest request) {
        carBusinessRules.checkIfCarExists(id);

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Araç bulunamadı: " + id));

        carMapper.updateCarFromRequest(request, car);
        return carMapper.toResponse(carRepository.save(car));
    }

    @Override
    @Transactional
    public void delete(String id) {
        carBusinessRules.checkIfCarExists(id);
        carRepository.deleteById(id);
    }

    private List<Car> sorted(List<Car> cars, CarSortOption sort) {
        if (sort == null) {
            return cars;
        }
        return switch (sort) {
            case PRICE_ASC  -> cars.stream().sorted(Comparator.comparing(Car::getPricePerDay)).toList();
            case PRICE_DESC -> cars.stream().sorted(Comparator.comparing(Car::getPricePerDay).reversed()).toList();
            case NEWEST     -> cars.stream().sorted(Comparator.comparing(Car::getYear).reversed()).toList();
            case RECOMMENDED -> cars.stream().sorted(Comparator.comparing(Car::getRating).reversed()).toList();
        };
    }
}
