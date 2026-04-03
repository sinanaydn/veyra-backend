package com.veyra.rentacar.features.rentals.entities;

import com.veyra.rentacar.core.utilities.CodeGenerator;
import com.veyra.rentacar.features.cars.entities.Car;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    // User modülü ayrı kurulacağı için şimdilik veritabanında sadece ID tutacak basit bir String kolon
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false, unique = true)
    private String reservationCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private LocalDateTime pickupDateTime;

    @Column(nullable = false)
    private String returnLocation;

    @Column(nullable = false)
    private LocalDateTime returnDateTime;

    @Column(nullable = false)
    private Integer days;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal deposit;

    @Column(precision = 10, scale = 2)
    private BigDecimal extrasTotal;

    // deposit dahil değil: subtotal + insurance + extrasTotal
    @Column(precision = 10, scale = 2)
    private BigDecimal grandTotal;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.reservationCode == null || this.reservationCode.isEmpty()) {
            this.reservationCode = CodeGenerator.generateReservationCode();
        }
    }
}
