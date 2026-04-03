package com.veyra.rentacar.features.rentals.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateRentalRequest {

    @NotBlank
    private String carId;

    @NotBlank
    private String pickupLocation;

    @NotNull
    private LocalDateTime pickupDateTime;

    @NotBlank
    private String returnLocation;

    @NotNull
    private LocalDateTime returnDateTime;

    @NotNull
    @Min(1)
    private Integer days;

    private BigDecimal subtotal;

    private BigDecimal deposit;

    private BigDecimal extrasTotal;

    private BigDecimal grandTotal;
}
