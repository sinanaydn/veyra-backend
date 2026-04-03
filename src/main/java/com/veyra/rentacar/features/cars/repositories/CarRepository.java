package com.veyra.rentacar.features.cars.repositories;

import com.veyra.rentacar.features.cars.entities.Car;
import com.veyra.rentacar.features.cars.entities.enums.AvailabilityStatus;
import com.veyra.rentacar.features.cars.entities.enums.FuelType;
import com.veyra.rentacar.features.cars.entities.enums.Transmission;
import com.veyra.rentacar.features.models.entities.CarCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, String> {

    Optional<Car> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Car> findByAvailability(AvailabilityStatus availability);

    List<Car> findByAvailabilityOrderByPricePerDayAsc(AvailabilityStatus availability);

    List<Car> findTopByAvailabilityOrderByRatingDesc(AvailabilityStatus availability, Pageable pageable);

    @Query("""
            SELECT c FROM Car c
            WHERE (:city IS NULL OR c.city = :city)
            AND (:brandId IS NULL OR c.brand.id = :brandId)
            AND (:modelId IS NULL OR c.model.id = :modelId)
            AND (:transmission IS NULL OR c.transmission = :transmission)
            AND (:fuelType IS NULL OR c.fuelType = :fuelType)
            AND (:category IS NULL OR c.category = :category)
            AND (:minSeats IS NULL OR c.seats >= :minSeats)
            AND (:minPrice IS NULL OR c.pricePerDay >= :minPrice)
            AND (:maxPrice IS NULL OR c.pricePerDay <= :maxPrice)
            AND (:airportEligible IS NULL OR c.airportEligible = :airportEligible)
            """)
    List<Car> findByFilters(
            @Param("city") String city,
            @Param("brandId") String brandId,
            @Param("modelId") String modelId,
            @Param("transmission") Transmission transmission,
            @Param("fuelType") FuelType fuelType,
            @Param("category") CarCategory category,
            @Param("minSeats") Integer minSeats,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("airportEligible") Boolean airportEligible
    );

    @Query("""
            SELECT c FROM Car c
            WHERE c.category = :category
            AND c.id <> :excludeId
            """)
    List<Car> findSimilarCars(
            @Param("category") CarCategory category,
            @Param("excludeId") String excludeId,
            Pageable pageable
    );
}
