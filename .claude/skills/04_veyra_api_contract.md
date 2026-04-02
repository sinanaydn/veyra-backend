---
name: veyra-api-contract
description: >
  Veyra frontend'inin backend'den beklediği API sözleşmesini uygular. Response
  oluşturulurken, endpoint yazılırken veya DTO tanımlanırken her zaman bu skill
  okunmalıdır. "Frontend bunu görecek mi", "ID nasıl dönmeli", "enum nasıl
  yazılmalı", "tarih formatı", "endpoint URL'i ne olmalı", "CORS ayarı",
  "auth response nasıl", "insurancePackages nerede", "reservationCode formatı"
  gibi ifadeler bu skill'i tetikler. Bu kurallara uyulmayan her satır frontend'i
  kırar — eksiksiz uygula.
---

# Veyra API Contract — Frontend Sözleşmesi

Bu dosya Veyra Next.js frontend'inin backend'den beklediği tam sözleşmedir.
Frontend `src/lib/mocks/` içindeki mock data'yı gerçek API'ye bağlayacak.
Aşağıdaki kurallardan herhangi birinin ihlali frontend'de runtime crash'e yol açar.

---

## 1. Kritik Tip Kuralları — Bunlar Frontend'i Crashler

### ID Tipi — Her Yerde String (UUID)

Tüm entity'lerde ID tipi `String` — UUID stratejisi ile üretilir.
DB'de `UUID` tipi, Java'da `String`, frontend'de `String` — hiçbir dönüşüm gerekmez.

```java
// ✅ DOĞRU — Entity'de String UUID
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;  // "550e8400-e29b-41d4-a716-446655440000"
}

// Response DTO — aynı tip, ekstra mapping yok
public class GetCarResponse {
    private String id;  // direkt geliyor, dönüşüm yok
}

// Repository — String PK ile çalışır
public interface CarRepository extends JpaRepository<Car, String> { }

// Mapper — id için özel mapping gerekmez, otomatik map edilir
@Mapper(componentModel = "spring")
public interface CarMapper {
    GetCarResponse toResponse(Car car);  // id String → String, sorunsuz
}
```

```java
// ❌ YANLIŞ — Long kullanma, frontend String bekliyor
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;  // dönüşüm gerektirir, hata riski yaratır
```

**Kural:** Tüm entity'lerde `@GeneratedValue(strategy = GenerationType.UUID)` kullanılır.
`JpaRepository<Entity, String>` — ikinci generic parametre her zaman `String`.
ID için mapper'da hiçbir özel mapping yazılmaz — otomatik çalışır.

---

### Enum Değerleri — Her Zaman UPPER_CASE String

```java
// ✅ DOĞRU — DB'de ve response'ta "GASOLINE" yazar
public enum FuelType {
    GASOLINE, DIESEL, HYBRID, ELECTRIC
}

public enum Transmission {
    AUTOMATIC, MANUAL
}

public enum AvailabilityStatus {
    AVAILABLE, RESERVED, MAINTENANCE
}

public enum CarCategory {
    ECONOMY, SEDAN, SUV, EXECUTIVE, VIP
}

public enum ReservationStatus {
    PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED
}

public enum UserRole {
    ADMIN, USER
}

public enum LocationType {
    OFFICE, AIRPORT
}

// Entity'de annotation zorunlu
@Enumerated(EnumType.STRING)  // "GASOLINE" yazar, 0 değil
@Column(nullable = false)
private FuelType fuelType;
```

```java
// ❌ YANLIŞ — küçük harf frontend'i crashler
// Frontend "GASOLINE" bekliyor, "gasoline" gelirse switch/case patlar
public enum FuelType {
    gasoline, diesel  // yasak
}
```

---

### Tarih Formatı — ISO 8601 String

Frontend tüm tarihleri `"2026-03-15T10:00:00.000Z"` formatında bekliyor.
`LocalDate` değil, `LocalDateTime` veya `Instant` kullanılır.
Jackson ayarı `application.yml`'de zaten yapılmış — ek bir şey yazmana gerek yok.

```java
// ✅ DOĞRU — Entity'de LocalDateTime
@Column(nullable = false)
private LocalDateTime pickupDateTime;  // ISO 8601 olarak serialize edilir

@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@PrePersist
private void setCreatedAt() {
    this.createdAt = LocalDateTime.now();
}
```

```java
// ❌ YANLIŞ — LocalDate zaman bilgisi taşımaz, frontend parse edemez
private LocalDate pickupDate;  // kullanılmaz

// ❌ YANLIŞ — timestamp sayı olarak gelir, frontend string bekliyor
// application.yml'de write-dates-as-timestamps: false ayarı var
// bu ayarı değiştirme
```

---

## 2. Car Response — insurancePackages ve extras Embedded Olmalı

Frontend `GET /api/v1/cars/{slug}` isteğinde `insurancePackages[]` ve `extras[]`
dizilerini Car objesinin **içinde** bekliyor. Ayrı endpoint yoktur.

```java
// ✅ DOĞRU — GetCarResponse içinde gömülü
public class GetCarResponse {
    private String id;
    private String slug;
    private String brandId;
    private String brandName;
    private String modelId;
    private String modelName;
    private Integer year;
    private String category;
    private String city;
    private List<String> pickupLocations;
    private List<String> returnLocations;
    private String transmission;
    private String fuelType;
    private Integer seats;
    private Integer baggage;
    private Integer doors;
    private List<String> imageUrls;        // dizi — tek string değil
    private BigDecimal pricePerDay;
    private BigDecimal depositAmount;
    private Integer mileageLimit;
    private String fuelPolicy;
    private String availability;
    private Boolean airportEligible;
    private String description;
    private List<String> features;
    private List<InsurancePackageResponse> insurancePackages;  // embedded
    private List<ExtraServiceResponse> extras;                 // embedded
    private Double rating;
    private Integer reviewCount;
}
```

```java
// ❌ YANLIŞ — ayrı endpoint açma
// GET /api/v1/cars/{id}/insurance  → bu endpoint olmayacak
// GET /api/v1/cars/{id}/extras     → bu endpoint olmayacak
```

---

## 3. Auth Response — user Objesi Zorunlu

```java
// ✅ DOĞRU — hem user hem token dönmeli
public class AuthResponse {
    private UserResponse user;   // zorunlu — sadece token dönmez
    private String token;
}

public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;              // "ADMIN" veya "USER"
    private String preferredLanguage; // "tr" veya "en"
    private String preferredCurrency; // "TRY", "EUR", "USD"
}
```

```java
// ❌ YANLIŞ — sadece token dönerse frontend user state'i kuramaz
public class AuthResponse {
    private String token;  // eksik — user objesi yok
}
```

Frontend login sonrası `AuthResponse.user`'ı Zustand store'a yazar.
`user` yoksa auth state oluşmaz, guard'lar çalışmaz.

---

## 4. ReservationCode Formatı

```java
// ✅ DOĞRU — "VYR-" prefix + 6 karakter uppercase
// Örnek: "VYR-K2M9NP", "VYR-ABC123"
String code = CodeGenerator.generateReservationCode();

// RentalManager içinde kullanımı
@Transactional
public GetRentalResponse create(CreateRentalRequest request) {
    rentalBusinessRules.checkIfCarIsAvailable(request.getCarId());

    Rental rental = rentalMapper.toEntity(request);
    rental.setReservationCode(CodeGenerator.generateReservationCode()); // zorunlu
    rental.setStatus(ReservationStatus.PENDING);
    rental.setCreatedAt(LocalDateTime.now());

    return rentalMapper.toResponse(rentalRepository.save(rental));
}
```

---

## 5. grandTotal Hesabı — Deposit Dahil Değil

```java
// Frontend fiyat kırılımı beklentisi:
// subtotal    = pricePerDay × rentalDays
// insurance   = insurance.pricePerDay × rentalDays (seçildiyse)
// extrasTotal = extra'ların toplamı (PER_DAY × days veya ONE_TIME)
// grandTotal  = subtotal + insurance + extrasTotal
// deposit     = ayrı gösterilir, grandTotal'a EKLENMEz

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
    private BigDecimal subtotal;      // araç ücreti
    private BigDecimal deposit;       // depozito — ayrı
    private BigDecimal extrasTotal;   // ek hizmetler
    private BigDecimal grandTotal;    // subtotal + insurance + extras (deposit HARİÇ)
    private LocalDateTime createdAt;
    // Araç bilgileri (frontend card'da gösterir)
    private String carBrandName;
    private String carModelName;
    private String carImageUrl;
}
```

---

## 6. imageUrls — Dizi Olmalı

```java
// ✅ DOĞRU — liste olarak sakla ve döndür
// Entity
@ElementCollection
@CollectionTable(name = "car_images", joinColumns = @JoinColumn(name = "car_id"))
@Column(name = "image_url")
private List<String> imageUrls = new ArrayList<>();

// Response
private List<String> imageUrls;  // ["url1", "url2", "url3"]
```

```java
// ❌ YANLIŞ — tek string dönerse frontend carousel çalışmaz
private String imageUrl;  // frontend List<String> bekliyor
```

---

## 7. Endpoint Listesi — Tam URL Haritası

Tüm endpoint'ler `/api/v1/` prefix'i ile başlar. Versiyonlama zorunludur.

### Auth Endpointleri

| Method | URL | Request | Response | Auth |
|--------|-----|---------|----------|------|
| POST | `/api/v1/auth/login` | `LoginRequest` | `AuthResponse` | Public |
| POST | `/api/v1/auth/register` | `RegisterRequest` | `AuthResponse` | Public |
| GET | `/api/v1/users/me` | — | `UserResponse` | Bearer Token |

### Car Endpointleri

| Method | URL | Request | Response | Auth |
|--------|-----|---------|----------|------|
| GET | `/api/v1/cars` | Query params | `List<GetCarResponse>` | Public |
| GET | `/api/v1/cars/featured` | `?limit=4` | `List<GetCarResponse>` | Public |
| GET | `/api/v1/cars/{slug}` | — | `GetCarResponse` | Public |
| GET | `/api/v1/cars/id/{id}` | — | `GetCarResponse` | Public |
| POST | `/api/v1/cars` | `CreateCarRequest` | `GetCarResponse` | ADMIN |
| PUT | `/api/v1/cars/{id}` | `UpdateCarRequest` | `GetCarResponse` | ADMIN |
| DELETE | `/api/v1/cars/{id}` | — | 204 No Content | ADMIN |

### Brand Endpointleri

| Method | URL | Request | Response | Auth |
|--------|-----|---------|----------|------|
| GET | `/api/v1/brands` | — | `List<GetBrandResponse>` | Public |
| GET | `/api/v1/brands/{id}` | — | `GetBrandResponse` | Public |
| POST | `/api/v1/brands` | `CreateBrandRequest` | `GetBrandResponse` | ADMIN |
| PUT | `/api/v1/brands/{id}` | `UpdateBrandRequest` | `GetBrandResponse` | ADMIN |
| DELETE | `/api/v1/brands/{id}` | — | 204 No Content | ADMIN |

### Model Endpointleri

| Method | URL | Request | Response | Auth |
|--------|-----|---------|----------|------|
| GET | `/api/v1/carmodels` | — | `List<GetCarModelResponse>` | Public |
| GET | `/api/v1/carmodels/brand/{brandId}` | — | `List<GetCarModelResponse>` | Public |
| POST | `/api/v1/carmodels` | `CreateCarModelRequest` | `GetCarModelResponse` | ADMIN |
| PUT | `/api/v1/carmodels/{id}` | `UpdateCarModelRequest` | `GetCarModelResponse` | ADMIN |
| DELETE | `/api/v1/carmodels/{id}` | — | 204 No Content | ADMIN |

### Rental Endpointleri

| Method | URL | Request | Response | Auth |
|--------|-----|---------|----------|------|
| GET | `/api/v1/rentals` | — | `List<GetRentalResponse>` | ADMIN |
| GET | `/api/v1/rentals/{id}` | — | `GetRentalResponse` | USER/ADMIN |
| GET | `/api/v1/rentals/user/{userId}` | — | `List<GetRentalResponse>` | USER/ADMIN |
| POST | `/api/v1/rentals` | `CreateRentalRequest` | `GetRentalResponse` | USER |
| PUT | `/api/v1/rentals/{id}/status` | `UpdateRentalStatusRequest` | `GetRentalResponse` | ADMIN |

### Dashboard Endpointleri (Admin Only)

| Method | URL | Response |
|--------|-----|----------|
| GET | `/api/v1/dashboard/kpis` | `DashboardKPIResponse` |
| GET | `/api/v1/dashboard/status-distribution` | `List<StatusDistributionResponse>` |
| GET | `/api/v1/dashboard/fleet-availability` | `List<FleetAvailabilityResponse>` |
| GET | `/api/v1/dashboard/city-performance` | `List<CityPerformanceResponse>` |
| GET | `/api/v1/dashboard/recent-reservations` | `List<GetRentalResponse>` |
| GET | `/api/v1/dashboard/recent-activity` | `List<RecentActivityResponse>` |

---

## 8. Car Filtre Parametreleri

Frontend `GET /api/v1/cars` isteğinde query param gönderir.
Tüm parametreler opsiyoneldir — null gelirse filtre uygulanmaz.

```java
// Controller'da @RequestParam ile al
@GetMapping
public ResponseEntity<List<GetCarResponse>> getAll(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String brandId,
        @RequestParam(required = false) String modelId,
        @RequestParam(required = false) Transmission transmission,
        @RequestParam(required = false) FuelType fuelType,
        @RequestParam(required = false) CarCategory category,
        @RequestParam(required = false) Integer minSeats,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) Boolean airportEligible,
        @RequestParam(required = false) AvailabilityStatus availability,
        @RequestParam(required = false, defaultValue = "RECOMMENDED") CarSortOption sort
) {
    return ResponseEntity.ok(carService.getAll(
        city, brandId, modelId, transmission, fuelType,
        category, minSeats, minPrice, maxPrice, airportEligible, availability, sort
    ));
}
```

---

## 9. CORS Ayarı

Frontend `http://localhost:3000`'den istek atar. CORS izni olmadan tüm
istekler tarayıcı tarafından bloke edilir.

```java
// SecurityConfig içinde — zaten 03_veyra_spring_patterns.md'de tanımlı
config.setAllowedOrigins(List.of("http://localhost:3000"));
```

Bu ayar `SecurityConfig.corsConfigurationSource()` içinde tanımlanmıştır.
Ayrıca `@CrossOrigin` annotation'ı controller'lara **eklenmez** —
merkezi CORS config yeterlidir.

---

## 10. Hata Response Formatı

Frontend başarısız isteklerde bu formatı bekler:

```json
{
  "message": "Araç bulunamadı: bmw-3-serisi-2024",
  "errors": null,
  "status": 400,
  "timestamp": "2026-03-15T10:00:00"
}
```

`GlobalExceptionHandler` bu formatı otomatik üretir.
Controller içinde `try/catch` bloğu yazmak yasaktır — tüm hatalar
handler'a bırakılır.

---

## Hızlı Kontrol Listesi — Response Yazarken Sor

- [ ] Entity'de `@GeneratedValue(strategy = GenerationType.UUID)` var mı?
- [ ] `JpaRepository<Entity, String>` — ikinci parametre `String` mi?
- [ ] Enum field'ları `@Enumerated(EnumType.STRING)` ile mi işaretli?
- [ ] Tarih alanları `LocalDateTime` mı? (`LocalDate` değil)
- [ ] `GetCarResponse` içinde `insurancePackages[]` ve `extras[]` var mı?
- [ ] `AuthResponse` içinde `user` objesi var mı?
- [ ] `Rental` response'unda `grandTotal` deposit içermiyor mu?
- [ ] `imageUrls` `List<String>` mı?
- [ ] `reservationCode` `"VYR-"` prefix'i ile mi başlıyor?
- [ ] Endpoint URL'i `/api/v1/` ile mi başlıyor?
- [ ] Controller'da `@CrossOrigin` annotation'ı yok mu? (merkezi CORS var)
- [ ] Controller'da `try/catch` bloğu yok mu? (GlobalExceptionHandler var)
