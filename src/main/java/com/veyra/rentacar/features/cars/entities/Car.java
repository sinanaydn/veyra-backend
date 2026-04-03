package com.veyra.rentacar.features.cars.entities;

import com.veyra.rentacar.core.utilities.SlugHelper;
import com.veyra.rentacar.features.brands.entities.Brand;
import com.veyra.rentacar.features.cars.entities.enums.AvailabilityStatus;
import com.veyra.rentacar.features.cars.entities.enums.FuelPolicy;
import com.veyra.rentacar.features.cars.entities.enums.FuelType;
import com.veyra.rentacar.features.cars.entities.enums.Transmission;
import com.veyra.rentacar.features.models.entities.CarCategory;
import com.veyra.rentacar.features.models.entities.CarModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel model;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarCategory category;

    @Column(nullable = false)
    private String city;

    @ElementCollection
    @CollectionTable(name = "car_pickup_locations", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "location")
    private List<String> pickupLocations = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "car_return_locations", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "location")
    private List<String> returnLocations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    @Column(nullable = false)
    private Integer seats;

    @Column(nullable = false)
    private Integer baggage;

    @Column(nullable = false)
    private Integer doors;

    @ElementCollection
    @CollectionTable(name = "car_images", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "price_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(name = "deposit_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "mileage_limit")
    private Integer mileageLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_policy", nullable = false)
    private FuelPolicy fuelPolicy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus availability = AvailabilityStatus.AVAILABLE;

    @Column(name = "airport_eligible", nullable = false)
    private Boolean airportEligible = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "car_features", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InsurancePackage> insurancePackages = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExtraService> extras = new ArrayList<>();

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @PrePersist
    protected void generateSlug() {
        if (this.slug == null || this.slug.isEmpty()) {
            // NullPointerException önlemi: İlişkisel nesneler tam dolu gelmediyse varsayılan güvenli atama yapar
            String brandName = (this.brand != null && this.brand.getName() != null) ? this.brand.getName() : "unknown-brand";
            String modelName = (this.model != null && this.model.getName() != null) ? this.model.getName() : "unknown-model";
            
            this.slug = SlugHelper.generate(
                    brandName,
                    modelName,
                    String.valueOf(this.year)
            );
        }
    }
}
