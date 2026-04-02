---
name: veyra-architecture
description: >
  Veyra rent-a-car backend projesinin paket yapısını, katman sorumluluklarını ve
  naming convention kurallarını uygular. Bu projeye yeni bir Java sınıfı, feature,
  entity, DTO, service, controller veya repository eklendiğinde her zaman bu skill
  okunmalıdır. "Car feature yaz", "Brand entity oluştur", "yeni domain ekle",
  "paket yapısı nedir", "nereye koyayım" gibi ifadeler bu skill'i tetikler.
  Hiçbir zaman rastgele paket ismi üretme — bu dosyadaki yapıya kesinlikle uy.
---

# Veyra Architecture — Paket Yapısı ve Katman Kuralları

Bu skill Veyra backend projesinin iskeletini tanımlar.
Yeni bir şey yazmadan önce bu haritayı oku, sınıfı doğru yere koy.

---

## Proje Kök Paketi

```
com.veyra.rentacar/
├── core/
└── features/
```

`core/` → kimseye ait değil, tüm feature'lar kullanır  
`features/` → her domain kendi klasöründe, birbirinden bağımsız

---

## core/ — Ortak Altyapı

```
core/
├── exception/
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── result/
│   └── ErrorResponse.java
├── security/
│   ├── SecurityConfig.java
│   ├── JwtFilter.java
│   └── JwtService.java
├── config/
│   ├── SwaggerConfig.java
│   └── CorsConfig.java
└── utilities/
    ├── SlugHelper.java
    └── CodeGenerator.java
```

**Kural:** `core/` içine feature'a özgü hiçbir şey girmez.
`CarBusinessRules`, `CarMapper` gibi sınıflar feature klasöründe olur.

---

## features/{domain}/ — Her Domain İçin Tam Yapı

Yeni bir domain eklerken aşağıdaki klasör yapısını **eksiksiz** oluştur.
Eksik klasör bırakma — kullanılmasa bile yapı kurulur.

```
features/
└── cars/                          ← domain adı (çoğul, küçük harf)
    ├── abstracts/
    │   └── CarService.java        ← interface buraya
    ├── concretes/
    │   └── CarManager.java        ← implements CarService buraya
    ├── rules/
    │   └── CarBusinessRules.java  ← validasyon ve iş kuralları
    ├── entities/
    │   └── Car.java               ← JPA entity
    ├── repositories/
    │   └── CarRepository.java     ← JpaRepository extend eden interface
    ├── dtos/
    │   ├── requests/
    │   │   ├── CreateCarRequest.java
    │   │   └── UpdateCarRequest.java
    │   └── responses/
    │       └── GetCarResponse.java
    ├── mappers/
    │   └── CarMapper.java         ← MapStruct interface, feature'a özgü
    └── controllers/
        └── CarsController.java    ← HTTP katmanı
```

### Mevcut Domain'ler

| Domain | Klasör |
|--------|--------|
| Kimlik doğrulama | `features/auth/` |
| Araçlar | `features/cars/` |
| Markalar | `features/brands/` |
| Modeller | `features/models/` |
| Kiralamalar | `features/rentals/` |
| Kullanıcılar | `features/users/` |
| Dashboard | `features/dashboard/` |

---

## Naming Convention — Kesin Kurallar

### Interface ve Implementation

```java
// ✅ DOĞRU
abstracts/CarService.java        → public interface CarService
concretes/CarManager.java        → public class CarManager implements CarService

// ❌ YANLIŞ — asla kullanma
concretes/CarServiceImpl.java    → "Impl" suffix yasak
concretes/CarServiceImplementation.java → bu da yasak
```

Kural basit: interface ismi `XxxService`, implementation ismi `XxxManager`.
`Impl` suffix bu projede yoktur.

### DTO İsimlendirmesi

```java
// Request DTO'ları — ne yapılıyor + Request
CreateCarRequest.java     // POST — yeni oluşturma
UpdateCarRequest.java     // PUT  — güncelleme

// Response DTO'ları — ne dönüyor + Response
GetCarResponse.java       // tek kayıt veya liste için aynı DTO
```

### Diğer Sınıflar

```java
CarBusinessRules.java     // rules/ altında — suffix: BusinessRules
CarMapper.java            // mappers/ altında — suffix: Mapper
CarRepository.java        // repositories/ altında — suffix: Repository
CarsController.java       // controllers/ altında — çoğul + Controller
Car.java                  // entities/ altında — sade entity adı
```

---

## Adım Adım İlerleme Kuralı

Bir feature yazarken sırayı takip et, adımları atlatma.
Her adımda kullanıcıya "Tamam mı, devam edelim mi?" diye sor.

```
1. Entity          → features/cars/entities/Car.java
2. Repository      → features/cars/repositories/CarRepository.java
3. DTO'lar         → requests/ ve responses/ altına
4. Mapper          → features/cars/mappers/CarMapper.java
5. Service (IF)    → features/cars/abstracts/CarService.java
6. Manager         → features/cars/concretes/CarManager.java
7. BusinessRules   → kullanıcı onayı olmadan oluşturma — adım 6 bittikten sonra sor
8. Controller      → features/cars/controllers/CarsController.java
```

`BusinessRules` sınıfını özellikle sormadan oluşturma.
"CarBusinessRules sınıfını oluşturalım mı?" diye bekle.

---

## Hızlı Referans — Neyin Nerede Olduğu

| Sınıf tipi | Paket | Örnek |
|-----------|-------|-------|
| JPA Entity | `features/{domain}/entities/` | `Car.java` |
| Spring Data Repository | `features/{domain}/repositories/` | `CarRepository.java` |
| Service Interface | `features/{domain}/abstracts/` | `CarService.java` |
| Service Implementation | `features/{domain}/concretes/` | `CarManager.java` |
| Business Validation | `features/{domain}/rules/` | `CarBusinessRules.java` |
| MapStruct Mapper | `features/{domain}/mappers/` | `CarMapper.java` |
| Request DTO | `features/{domain}/dtos/requests/` | `CreateCarRequest.java` |
| Response DTO | `features/{domain}/dtos/responses/` | `GetCarResponse.java` |
| REST Controller | `features/{domain}/controllers/` | `CarsController.java` |
| Global Exception | `core/exception/` | `GlobalExceptionHandler.java` |
| JWT İşlemleri | `core/security/` | `JwtService.java` |
| Yardımcı Araçlar | `core/utilities/` | `SlugHelper.java` |
| Swagger, CORS | `core/config/` | `SwaggerConfig.java` |
