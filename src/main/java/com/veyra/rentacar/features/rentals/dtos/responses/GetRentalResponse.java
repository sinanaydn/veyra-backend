package com.veyra.rentacar.features.rentals.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetRentalResponse {

    private String id;
    private String reservationCode;

    private String carId;
    private String userId;

    private String status;

    private String pickupLocation;
    private LocalDateTime pickupDateTime;

    private String returnLocation;
    private LocalDateTime returnDateTime;

    private Integer days;

    private BigDecimal subtotal;
    private BigDecimal deposit;
    private BigDecimal extrasTotal;
    private BigDecimal grandTotal;

    private LocalDateTime createdAt;

    private String carBrandName;
    private String carModelName;
    private String carImageUrl;
}
