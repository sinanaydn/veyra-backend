package com.veyra.rentacar.features.models.repositories;

import com.veyra.rentacar.features.models.entities.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, String> {

    List<CarModel> findByBrandId(String brandId);

    boolean existsByNameAndBrandId(String name, String brandId);
}
