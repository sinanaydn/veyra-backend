---
name: veyra-solid-java
description: >
  Veyra backend projesinde Java 25 ile SOLID prensiplerini ve OOP kurallarını
  uygular. Yeni bir sınıf yazılırken, mevcut sınıf düzenlenirken veya kod
  incelenirken her zaman bu skill okunmalıdır. "Sınıf yaz", "injection nasıl
  olacak", "@Autowired kullanayım mı", "bu doğru mu", "SOLID'e uygun mu",
  "bağımlılık nasıl eklenir", "bu sınıf çok büyüdü" gibi ifadeler bu skill'i
  tetikler. Bu projedeki her Java sınıfı bu dosyadaki kurallara uymak zorundadır.
---

# Veyra SOLID & OOP Kuralları — Java 25

Her sınıf yazılmadan önce bu dosyadaki kurallara göre tasarla.
Kural ihlali gördüğünde düzelt ve kullanıcıya neden değiştirdiğini açıkla.

---

## 1. Bağımlılık Enjeksiyonu — Constructor Injection Zorunlu

Bu projede bağımlılık enjeksiyonunun tek yöntemi constructor injection'dır.
Lombok'un `@RequiredArgsConstructor` annotation'ı ile constructor elle yazılmaz.

```java
// ✅ DOĞRU — Veyra standardı
@Service
@RequiredArgsConstructor
public class CarManager implements CarService {

    private final CarRepository carRepository;
    private final CarBusinessRules carBusinessRules;
    private final CarMapper carMapper;

    // Constructor'ı elle yazma — @RequiredArgsConstructor halleder
}
```

```java
// ❌ YANLIŞ — @Autowired field injection — kesinlikle yasak
@Service
public class CarManager implements CarService {

    @Autowired  // bu annotation bu projede kullanılmaz
    private CarRepository carRepository;
}
```

```java
// ❌ YANLIŞ — Setter injection da kullanılmaz
@Service
public class CarManager implements CarService {

    private CarRepository carRepository;

    @Autowired
    public void setCarRepository(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
}
```

**Neden constructor injection?**
- Bağımlılıklar `final` olduğu için immutable — sonradan değiştirilemez
- Test yazarken mock inject etmek kolay — Spring container gerekmez
- Circular dependency derleme zamanında yakalanır — runtime'da değil
- `@Autowired` Spring 4.3+ sürümlerinde zaten önerilmiyor

---

## 2. Single Responsibility — Her Sınıf Tek İş Yapar

Her katmanın tek bir sorumluluğu var. Bu sorumluluklar birbirine karışmaz.

| Sınıf | Tek Sorumluluğu |
|-------|-----------------|
| `CarsController` | HTTP isteği al, service'e ilet, response dön |
| `CarManager` | İş akışını koordine et — BusinessRules → Mapper → Repository |
| `CarBusinessRules` | Validasyon ve iş kurallarını kontrol et, ihlalde exception fırlat |
| `CarMapper` | Entity ↔ DTO dönüşümü yap |
| `CarRepository` | Veritabanı işlemlerini gerçekleştir |

```java
// ✅ DOĞRU — CarManager sadece koordinatör
@Service
@RequiredArgsConstructor
public class CarManager implements CarService {

    private final CarRepository carRepository;
    private final CarBusinessRules carBusinessRules;
    private final CarMapper carMapper;

    @Override
    @Transactional
    public GetCarResponse create(CreateCarRequest request) {
        carBusinessRules.checkIfBrandExists(request.getBrandId()); // validasyon
        Car car = carMapper.toEntity(request);                      // dönüşüm
        Car saved = carRepository.save(car);                        // kayıt
        return carMapper.toResponse(saved);                         // dönüşüm
    }
}
```

```java
// ❌ YANLIŞ — CarManager her şeyi yapıyor
@Service
public class CarManager implements CarService {

    @Override
    public GetCarResponse create(CreateCarRequest request) {
        // Validasyon burada — BusinessRules'un işi
        if (!brandRepository.existsById(request.getBrandId())) {
            throw new BusinessException("Marka bulunamadı");
        }
        // Manuel mapping burada — Mapper'ın işi
        Car car = new Car();
        car.setBrandId(request.getBrandId());
        car.setModelId(request.getModelId());
        // ... 20 satır daha setter
        // Email gönderimi burada — EmailService'in işi
        emailService.sendNotification(request.getEmail());
        return carRepository.save(car);  // Entity dönüyor — DTO değil
    }
}
```

---

## 3. God Class Yasağı

Bir Manager sınıfı büyümeye başlarsa sorumlulukları dağıt.
Aşağıdaki belirtilerden biri varsa sınıfı böl:

- Başka bir domain'in repository'sine inject ediliyorsa
- Dosya 150 satırı geçiyorsa
- Metodların yarısı birbiriyle ilgisizse
- Sınıf adını tek cümleyle açıklamak zor geliyorsa

```java
// ❌ YANLIŞ — CarManager her şeyi yönetiyor
@Service
@RequiredArgsConstructor
public class CarManager implements CarService {

    private final CarRepository carRepository;
    private final BrandRepository brandRepository;   // başka domain
    private final UserRepository userRepository;     // başka domain
    private final EmailService emailService;         // başka domain

    public List<GetBrandResponse> getAllBrands() { ... }  // Brand işi
    public void sendEmail(String to) { ... }              // Email işi
    public User getCurrentUser() { ... }                  // User işi
}
```

```java
// ✅ DOĞRU — Her Manager kendi domain'inde
@Service @RequiredArgsConstructor
public class CarManager implements CarService {
    private final CarRepository carRepository;
    private final CarBusinessRules carBusinessRules;
    private final CarMapper carMapper;
    // Sadece Car ile ilgili metodlar
}

@Service @RequiredArgsConstructor
public class BrandManager implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandBusinessRules brandBusinessRules;
    private final BrandMapper brandMapper;
    // Sadece Brand ile ilgili metodlar
}
```

---

## 4. Interface'e Programla — Implementation'a Değil

Controller ve diğer sınıflar her zaman interface tipini kullanır,
concrete sınıfı değil. Spring bu injection'ı zaten doğru yapar.

```java
// ✅ DOĞRU — interface tipi inject ediliyor
@RestController
@RequiredArgsConstructor
public class CarsController {
    private final CarService carService;  // CarManager değil, CarService
}
```

```java
// ❌ YANLIŞ — concrete sınıf inject ediliyor
@RestController
@RequiredArgsConstructor
public class CarsController {
    private final CarManager carManager;  // implementation'a bağlanma
}
```

Bu kural sayesinde `CarManager` yerine farklı bir implementasyon gelirse
`CarsController` hiç değişmez.

---

## 5. Open/Closed — Mevcut Sınıfı Değiştirme, Yeni Ekle

Yeni bir iş kuralı eklenecekse mevcut Manager metodunu değiştirme.
Bunun yerine `BusinessRules` sınıfına yeni bir metod ekle.

```java
// ✅ DOĞRU — yeni kural yeni metod olarak eklendi
@Component
@RequiredArgsConstructor
public class CarBusinessRules {

    private final CarRepository carRepository;

    public void checkIfCarExists(String id) {
        if (!carRepository.existsById(Long.valueOf(id))) {
            throw new BusinessException("Araç bulunamadı: " + id);
        }
    }

    // Yeni kural eklendi — mevcut metod dokunulmadı
    public void checkIfCarIsAvailable(String id) {
        Car car = carRepository.findById(Long.valueOf(id)).orElseThrow();
        if (car.getAvailability() != AvailabilityStatus.AVAILABLE) {
            throw new BusinessException("Araç şu an müsait değil");
        }
    }
}
```

---

## 6. Erken Return — İç İçe Kod Yazma

Koşullu bloklarda iç içe `if` yazmak yerine erken return kullan.
Bu hem okunabilirliği artırır hem de tek sorumluluk prensibini destekler.

```java
// ❌ YANLIŞ — iç içe if blokları
public GetCarResponse getBySlug(String slug) {
    if (slug != null) {
        if (!slug.isEmpty()) {
            Optional<Car> car = carRepository.findBySlug(slug);
            if (car.isPresent()) {
                return carMapper.toResponse(car.get());
            } else {
                throw new BusinessException("Araç bulunamadı");
            }
        }
    }
    throw new BusinessException("Slug boş olamaz");
}
```

```java
// ✅ DOĞRU — erken return, düz akış
public GetCarResponse getBySlug(String slug) {
    carBusinessRules.checkIfSlugValid(slug);  // validasyon BusinessRules'ta

    return carRepository.findBySlug(slug)
            .map(carMapper::toResponse)
            .orElseThrow(() -> new BusinessException("Araç bulunamadı: " + slug));
}
```

---

## 7. Immutability — Mümkün Olan Her Yerde final

```java
// ✅ DOĞRU — field'lar final, değiştirilemez
@Service
@RequiredArgsConstructor
public class CarManager implements CarService {
    private final CarRepository carRepository;    // final zorunlu
    private final CarBusinessRules carBusinessRules;
    private final CarMapper carMapper;
}
```

```java
// ✅ DOĞRU — local variable'lar da final olabilir
public GetCarResponse getById(String id) {
    final Car car = carRepository.findById(Long.valueOf(id))
            .orElseThrow(() -> new BusinessException("Araç bulunamadı"));
    return carMapper.toResponse(car);
}
```

---

## Hızlı Kontrol Listesi — Sınıf Yazarken Sor

Yeni bir sınıf yazarken veya mevcut sınıfı düzenlerken bu soruları sor:

- [ ] `@Autowired` var mı? → `@RequiredArgsConstructor` + `private final` yap
- [ ] `*Impl` suffix var mı? → `*Manager` yap
- [ ] Başka domain'in repository'si inject ediliyor mu? → ayrı servis yaz
- [ ] Constructor elle yazılmış mı? → `@RequiredArgsConstructor` kullan
- [ ] Interface yerine concrete sınıf mı inject ediliyor? → interface'e çevir
- [ ] İç içe `if` bloğu 2 seviyeyi geçiyor mu? → erken return + BusinessRules
- [ ] Bir metod 20 satırı geçiyor mu? → private yardımcı metoda böl
- [ ] Sınıf 150 satırı geçiyor mu? → sorumluluk analizi yap, gerekirse böl
