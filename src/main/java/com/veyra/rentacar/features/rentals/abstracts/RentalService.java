package com.veyra.rentacar.features.rentals.abstracts;

import com.veyra.rentacar.features.rentals.dtos.requests.CreateRentalRequest;
import com.veyra.rentacar.features.rentals.dtos.requests.UpdateRentalStatusRequest;
import com.veyra.rentacar.features.rentals.dtos.responses.GetRentalResponse;
import com.veyra.rentacar.features.rentals.dtos.responses.RentalStatsResponse;
import com.veyra.rentacar.features.rentals.entities.ReservationStatus;

import java.util.List;

public interface RentalService {

    List<GetRentalResponse> getAll();

    GetRentalResponse getById(String id);

    List<GetRentalResponse> getByUserId(String userId);

    List<GetRentalResponse> getByStatus(ReservationStatus status);

    GetRentalResponse create(CreateRentalRequest request);

    GetRentalResponse updateStatus(String id, UpdateRentalStatusRequest request);

    RentalStatsResponse getStats();
}
