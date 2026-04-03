package com.veyra.rentacar.features.rentals.repositories;

import com.veyra.rentacar.features.rentals.entities.Rental;
import com.veyra.rentacar.features.rentals.entities.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {

    List<Rental> findByUserId(String userId);

    List<Rental> findByStatus(ReservationStatus status);

    List<Rental> findByCarId(String carId);

    boolean existsByCarIdAndStatusIn(String carId, List<ReservationStatus> statuses);

    @Query("SELECT SUM(r.grandTotal) FROM Rental r WHERE r.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();
}
