---
name: veyra-spring-patterns
description: >
  Veyra backend projesinde Spring Boot 4, Spring Security 7, JPA, MapStruct ve
  Lombok annotation'larının doğru kullanım kalıplarını uygular. Controller,
  Manager, Repository, Mapper, Entity yazarken veya düzenlerken her zaman bu
  skill okunmalıdır. "@Transactional nereye", "mapper nasıl yazılır",
  "entity ilişkisi nasıl kurulur", "security config", "jwt filter", "slug üretimi",
  "reservation code", "Lombok hangi annotation", "FetchType ne olmalı",
  "repository nasıl yazılır" gibi ifadeler bu skill'i tetikler.
  Spring Boot'u yarım kullanma — bu dosyadaki kalıpları eksiksiz uygula.
---

# Veyra Spring Patterns — Spring Boot 4 Kullanım Kuralları

Her Spring bileşeni yazılmadan önce ilgili bölümü oku.
Bu dosya "nasıl yazılır" sorusunun cevabıdır — "nereye konur" sorusu için
`01_veyra_architecture.md`'yi oku.

---

## 1. Lombok — Hangi Annotation Nerede Kullanılır

Üç farklı constructor annotation vardır. Yanlış kullanım runtime hatasına yol açar.

| Annotation | Ürettiği Constructor | Nerede Kullanılır |
|-----------|---------------------|-------------------|
| `@RequiredArgsConstructor` | Sadece `final` field'ları alır | `@Service`, `@Component`, `@RestController` |
| `@NoArgsConstructor` | Parametresiz | `@Entity` — Hibernate zorunlu kılar |
| `@AllArgsConstructor` | Tüm field'ları alır | `@Entity` — `@NoArgsConstructor` ile birlikte |

```java
// ✅ Entity — ikisi birden zorunlu
// Hibernate @NoArgsConstructor ile nesne oluşturur,
// @AllArgsConstructor test ve builder için kullanılır
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String slug;
}
```

```java
// ✅ Service — sadece @RequiredArgsConstructor
// Yeni bağımlılık eklemek için sadece private final yaz,
// constructor otomatik güncellenir
@Service
@RequiredArgsConstructor
public class CarManager implements CarService {
    private final CarRepository carRepository;
    private final CarBusinessRules carBusinessRules;
    private final CarMapper carMapper;
}
```

```java
// ✅ Controller — sadece @RequiredArgsConstructor
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarsController {
    private final CarService carService;
}
```

---

## 2. Entity Yazım Kuralları

### Temel Entity Yapısı

```java
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
    private String id;  // UUID — "550e8400-e29b-41d4-a716-446655440000"

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)  // DB'de "GASOLINE" yazar, sayı değil
    @Column(nullable = false)
    private FuelType fuelType;

    @Column(name = "price_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @ManyToOne(fetch = FetchType.LAZY)  // EAGER asla kullanılmaz
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InsurancePackage> insurancePackages = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExtraService> extras = new ArrayList<>();

    // Slug @PrePersist ile otomatik üretilir — elle set edilmez
    @PrePersist
    private void generateSlug() {
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = SlugHelper.generate(
                this.brand.getName(),
                this.modelName,
                this.year
            );
        }
    }
}
```

### JPA İlişki Kuralları

```java
// ✅ FetchType.LAZY — her ilişkide zorunlu
@ManyToOne(fetch = FetchType.LAZY)
private Brand brand;

// ❌ FetchType.EAGER — asla kullanılmaz
// Her Car yüklendiğinde Brand de yüklenir — N+1 problemi çıkar
@ManyToOne(fetch = FetchType.EAGER)
private Brand brand;
```

```java
// ✅ OneToMany — orphanRemoval ile birlikte
// Car silinince InsurancePackage'lar da silinir
@OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
private List<InsurancePackage> insurancePackages = new ArrayList<>();
```

```java
// ✅ Gerektiğinde @EntityGraph ile LAZY'yi override et
// N+1 olmadan ilişkili veriyi tek sorguda çek
@EntityGraph(attributePaths = {"brand", "insurancePackages", "extras"})
Optional<Car> findBySlug(String slug);
```

### Enum'lar Her Zaman STRING Olarak Saklanır

```java
// ✅ DOĞRU — DB'de "GASOLINE" yazar
@Enumerated(EnumType.STRING)
private FuelType fuelType;

// ❌ YANLIŞ — DB'de 0, 1, 2 yazar — enum sırası değişirse veri bozulur
@Enumerated(EnumType.ORDINAL)
private FuelType fuelType;
```

---

## 3. Repository Yazım Kuralları

### Temel Yapı

```java
// JpaRepository<Entity, ID tipi> — ID tipi her zaman String (UUID)
@Repository
public interface CarRepository extends JpaRepository<Car, String> {

    // Spring derived query — @Query yazmaya gerek yok
    Optional<Car> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Car> findByAvailability(AvailabilityStatus availability);

    List<Car> findByCityAndAvailabilityOrderByPricePerDayAsc(
        String city,
        AvailabilityStatus availability
    );

    // Sadece karmaşık sorgular için @Query kullan
    @Query("""
        SELECT c FROM Car c
        WHERE c.pricePerDay BETWEEN :minPrice AND :maxPrice
        AND (:city IS NULL OR c.city = :city)
        AND (:transmission IS NULL OR c.transmission = :transmission)
        ORDER BY c.pricePerDay ASC
        """)
    List<Car> findByFilters(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("city") String city,
        @Param("transmission") Transmission transmission
    );
}
```

**Kural:** Önce Spring'in derived query özelliğini dene.
`findBy`, `existsBy`, `countBy`, `deleteBy` metodları `@Query` gerektirmez.
Yalnızca birden fazla opsiyonel filtre veya JOIN gerektiren sorgularda `@Query` kullan.

---

## 4. Manager (@Service) Yazım Kuralları

### Temel Yapı ve @Transactional Kuralları

```java
@Service
@RequiredArgsConstructor
public class CarManager implements CarService {

    private final CarRepository carRepository;
    private final CarBusinessRules carBusinessRules;
    private final CarMapper carMapper;

    // Okuma işlemleri — readOnly = true performans kazanımı sağlar
    // Hibernate dirty checking devre dışı kalır, flush yapılmaz
    @Override
    @Transactional(readOnly = true)
    public GetCarResponse getBySlug(String slug) {
        return carRepository.findBySlug(slug)
                .map(carMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Araç bulunamadı: " + slug));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetCarResponse> getAll() {
        return carMapper.toResponseList(carRepository.findAll());
    }

    // Yazma işlemleri — standart @Transactional
    // Hata olursa tüm işlem geri alınır
    @Override
    @Transactional
    public GetCarResponse create(CreateCarRequest request) {
        // 1. BusinessRules — her zaman ilk sırada
        carBusinessRules.checkIfBrandExists(request.getBrandId());
        carBusinessRules.checkIfModelExists(request.getModelId());

        // 2. Mapper — entity'ye dönüştür
        Car car = carMapper.toEntity(request);

        // 3. Repository — kaydet
        Car saved = carRepository.save(car);

        // 4. Response döndür
        return carMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public GetCarResponse update(String id, UpdateCarRequest request) {
        // 1. BusinessRules
        carBusinessRules.checkIfCarExists(id);

        // 2. Mevcut entity'yi getir — ID artık String (UUID)
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Araç bulunamadı"));

        // 3. Mapper ile güncelle — null alanlar korunur
        carMapper.updateCarFromRequest(request, car);

        // 4. Kaydet ve dön
        return carMapper.toResponse(carRepository.save(car));
    }

    @Override
    @Transactional
    public void delete(String id) {
        carBusinessRules.checkIfCarExists(id);
        carRepository.deleteById(id);  // String ID — dönüşüm yok
    }
}
```

### Manager İçinde İş Akışı Sırası — Asla Değişmez

```
1. BusinessRules  →  validasyon ve iş kuralları kontrol et
2. Mapper         →  request'ten entity'ye veya entity'den response'a çevir
3. Repository     →  veritabanı işlemini gerçekleştir
4. return         →  response DTO döndür
```

---

## 5. BusinessRules Yazım Kuralları

Her `XxxBusinessRules` sınıfı **sadece kendi domain'inin repository'sini** bilir.
Başka domain'in repository'sine veya service'ine dokunamaz — bu SOLID ihlalidir.
Farklı domain validasyonu gerekiyorsa o domain'in `BusinessRules`'ı ayrıca yazılır
ve `Manager` ikisini birden inject ederek çağırır.

```java
// ✅ DOĞRU — CarBusinessRules sadece CarRepository'yi bilir
@Component
@RequiredArgsConstructor
public class CarBusinessRules {

    private final CarRepository carRepository; // sadece kendi repository'si

    // Kural metod isimleri "checkIf" ile başlar
    // ID artık String (UUID) — Long.valueOf dönüşümü yok
    public void checkIfCarExists(String id) {
        if (!carRepository.existsById(id)) {
            throw new BusinessException("Araç bulunamadı: " + id);
        }
    }

    public void checkIfSlugUnique(String slug) {
        if (carRepository.existsBySlug(slug)) {
            throw new BusinessException("Bu slug zaten kullanımda: " + slug);
        }
    }

    public void checkIfCarIsAvailable(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Araç bulunamadı"));
        if (car.getAvailability() != AvailabilityStatus.AVAILABLE) {
            throw new BusinessException("Araç şu an müsait değil");
        }
    }
}
```

```java
// ✅ DOĞRU — Brand validasyonu BrandBusinessRules'a aittir
@Component
@RequiredArgsConstructor
public class BrandBusinessRules {

    private final BrandRepository brandRepository; // sadece kendi repository'si

    public void checkIfBrandExists(String id) {
        if (!brandRepository.existsById(id)) {
            throw new BusinessException("Marka bulunamadı: " + id);
        }
    }
}
```

```java
// ✅ DOĞRU — Manager farklı domain'lerin BusinessRules'larını inject edebilir
// Bu koordinatörlük görevi — Manager'ın tek yetkisi budur
@Service
@RequiredArgsConstructor
public class CarManager implements CarService {

    private final CarRepository carRepository;
    private final CarBusinessRules carBusinessRules;
    private final BrandBusinessRules brandBusinessRules; // başka domain'in Rules'ı — normal
    private final CarMapper carMapper;

    @Override
    @Transactional
    public GetCarResponse create(CreateCarRequest request) {
        brandBusinessRules.checkIfBrandExists(request.getBrandId()); // Brand kuralı
        carBusinessRules.checkIfSlugUnique(request.getSlug());       // Car kuralı

        Car car = carMapper.toEntity(request);
        return carMapper.toResponse(carRepository.save(car));
    }
}
```

```java
// ❌ YANLIŞ — CarBusinessRules başka domain'in repository'sine dokunamaz
@Component
@RequiredArgsConstructor
public class CarBusinessRules {

    private final CarRepository carRepository;
    private final BrandRepository brandRepository; // SOLID ihlali — yasak

    public void checkIfBrandExists(String brandId) {
        // Bu kontrol BrandBusinessRules'a ait, buraya girmez
        if (!brandRepository.existsById(Long.valueOf(brandId))) {
            throw new BusinessException("Marka bulunamadı");
        }
    }
}
```

**Kural özeti:**

| Yapabilir | Yapamaz |
|-----------|---------|
| `CarManager` → `BrandBusinessRules` inject eder | `CarBusinessRules` → `BrandRepository` inject eder |
| `CarManager` → `BrandService` inject eder | `CarBusinessRules` → `BrandService` inject eder |
| Her `XxxBusinessRules` sadece kendi `XxxRepository`'sini bilir | `XxxBusinessRules` başka domain'e dokunamaz |

`BusinessRules` sınıfı sadece kontrol eder ve gerektiğinde `BusinessException`
fırlatır. Kaydetme, güncelleme gibi yazma işlemi yapmaz.

---

## 6. Controller Yazım Kuralları

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
@Tag(name = "Cars", description = "Araç yönetimi")  // Swagger
public class CarsController {

    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<GetCarResponse>> getAll() {
        return ResponseEntity.ok(carService.getAll());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<GetCarResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(carService.getBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<GetCarResponse> create(
            @RequestBody @Valid CreateCarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetCarResponse> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateCarRequest request) {
        return ResponseEntity.ok(carService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
```

### Controller Kuralları

- `void` dönen metod yoktur — DELETE hariç, o `ResponseEntity<Void>` döner
- İş mantığı, validasyon, hesaplama controller'a girmez
- `@Valid` annotation request body'de her zaman kullanılır
- `this.carService` yerine `carService` — `this` kullanılmaz
- Her endpoint bir `ResponseEntity<XxxResponse>` döner
- URL versiyonlama zorunlu: `/api/v1/...`

---

## 7. MapStruct Yazım Kuralları

```java
@Mapper(
    componentModel = "spring",           // Spring bean olarak inject edilir
    unmappedTargetPolicy = ReportingPolicy.IGNORE  // eksik field hata vermez
)
public interface CarMapper {

    // Request → Entity
    Car toEntity(CreateCarRequest request);

    // Entity → Response
    // ID String → String — UUID olduğu için özel mapping gerekmez
    // Nested field mapping gerekiyorsa belirt
    @Mapping(target = "brandName", source = "brand.name")
    GetCarResponse toResponse(Car car);

    // Liste dönüşümü — ayrıca implement etmeye gerek yok
    List<GetCarResponse> toResponseList(List<Car> cars);

    // Güncelleme — null gelen alanlar mevcut değeri silmez
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCarFromRequest(UpdateCarRequest request, @MappingTarget Car car);
}
```

**Mapper Kuralları:**

- Her feature'ın kendi mapper'ı vardır — `core/mapper/` diye bir yer yoktur
- `componentModel = "spring"` zorunludur — `Mappers.getMapper()` kullanılmaz
- ID artık UUID `String` — `String.valueOf()` dönüşümü yoktur, otomatik map edilir
- `@MappingTarget` ile update işleminde mevcut entity üzerine yazılır
- Nested nesneler (Brand, InsurancePackage) MapStruct tarafından otomatik handle edilir

---

## 8. Exception ve GlobalExceptionHandler

```java
// core/exception/BusinessException.java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

```java
// core/exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // İş kuralı ihlali — 400 Bad Request
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    // Bean Validation hatası — 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ErrorResponse error = ErrorResponse.builder()
                .message("Validasyon hatası")
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    // Beklenmeyen hata — 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .message("Sunucu hatası oluştu")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.internalServerError().body(error);
    }
}
```

```java
// core/result/ErrorResponse.java
@Data
@Builder
public class ErrorResponse {
    private String message;
    private List<String> errors;
    private int status;
    private LocalDateTime timestamp;
}
```

---

## 9. Utilities — SlugHelper ve CodeGenerator

```java
// core/utilities/SlugHelper.java
public final class SlugHelper {

    private SlugHelper() {}

    public static String generate(String... parts) {
        return Arrays.stream(parts)
                .filter(p -> p != null && !p.isEmpty())
                .map(SlugHelper::normalize)
                .collect(Collectors.joining("-"));
    }

    private static String normalize(String input) {
        return input.toLowerCase(Locale.ROOT)
                .replace("ş", "s").replace("ğ", "g")
                .replace("ü", "u").replace("ö", "o")
                .replace("ı", "i").replace("ç", "c")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
// Kullanım: SlugHelper.generate("BMW", "3 Serisi", "2024") → "bmw-3-serisi-2024"
```

```java
// core/utilities/CodeGenerator.java
public final class CodeGenerator {

    private CodeGenerator() {}

    // "VYR-" + 6 karakter uppercase alphanumeric
    public static String generateReservationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder("VYR-");
        Random random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();  // örnek: "VYR-K2M9NP"
    }
}
```

---

## 10. Security — JWT Filter ve Config

```java
// core/security/JwtFilter.java
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;  // Erken return — token yoksa devam et
        }

        final String token = authHeader.substring(7);
        final String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

```java
// core/security/SecurityConfig.java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpointler
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/api/v1/cars/**",
                    "/api/v1/brands/**",
                    "/api/v1/carmodels/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**"
                ).permitAll()
                // Sadece kullanıcı
                .requestMatchers(
                    "/api/v1/rentals/**",
                    "/api/v1/users/me"
                ).hasAnyRole("USER", "ADMIN")
                // Sadece admin
                .requestMatchers("/api/v1/admin/**",
                    "/api/v1/dashboard/**"
                ).hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        CorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

## Hızlı Kontrol Listesi — Spring Bileşeni Yazarken Sor

- [ ] Entity'de `@NoArgsConstructor` var mı? (Hibernate zorunlu)
- [ ] Service/Controller'da `@RequiredArgsConstructor` + `private final` var mı?
- [ ] `@Autowired` kullanılmış mı? → sil, `private final` yap
- [ ] FetchType `EAGER` var mı? → `LAZY`'ye çevir
- [ ] Enum `@Enumerated(EnumType.STRING)` var mı?
- [ ] Okuma metodunda `@Transactional(readOnly = true)` var mı?
- [ ] Manager'da sıra doğru mu? BusinessRules → Mapper → Repository → return
- [ ] Controller `void` mu dönüyor? → `ResponseEntity<XxxResponse>` yap
- [ ] Mapper'da `componentModel = "spring"` var mı?
- [ ] Entity'de `@GeneratedValue(strategy = GenerationType.UUID)` var mı?
- [ ] `JpaRepository<Entity, String>` — ikinci generic parametre `String` mi?
- [ ] Mapper'da ID için `String.valueOf()` yazılmış mı? → kaldır, UUID otomatik map edilir
- [ ] `@PrePersist` ile slug otomatik üretiliyor mu?
- [ ] `reservationCode` `CodeGenerator.generateReservationCode()` ile üretiliyor mu?
