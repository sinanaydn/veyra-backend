---
name: veyra-java-reviewer
description: >
  Veyra backend projesinde yazılan Java Spring Boot kodunu inceler ve proje
  kurallarına uygunluğunu kontrol eder. "Kodu incele", "review et", "doğru mu",
  "kontrol et", "bu kod uygun mu", "SOLID'e uyuyor mu", "hata var mı",
  "eksik ne", "bu pattern doğru mu" gibi ifadeler bu skill'i tetikler.
  Herhangi bir sınıf yazıldıktan sonra veya mevcut kod düzenlendiğinde
  otomatik olarak bu skill devreye girmeli ve aşağıdaki kontrol listesini
  eksiksiz uygulamalıdır. Sorun bulunursa düzelt ve neden yanlış olduğunu açıkla.
---

# Veyra Java Reviewer — Kod İnceleme Kuralları

Yazılan her Java sınıfını bu dosyadaki kurallara göre incele.
Sorun bulduğunda düzelt — sadece raporlama yapma.
Neden yanlış olduğunu ve nasıl düzeltildiğini kısaca açıkla.

Detaylı kural açıklamaları için ilgili skill dosyalarına bak:
- Paket yapısı → `01_veyra_architecture.md`
- SOLID ve OOP → `02_veyra_solid_java.md`
- Spring kalıpları → `03_veyra_spring_patterns.md`
- Frontend sözleşmesi → `04_veyra_api_contract.md`

---

## Bölüm 1 — Mimari ve Paket Yapısı Kontrolü

Her sınıf için önce şunu sor: **"Bu sınıf doğru yerde mi?"**

```
✅ Kontrol listesi:

[ ] Sınıf doğru klasörde mi?
    - Interface      → features/{domain}/abstracts/
    - Implementation → features/{domain}/concretes/
    - BusinessRules  → features/{domain}/rules/
    - Entity         → features/{domain}/entities/
    - Repository     → features/{domain}/repositories/
    - Request DTO    → features/{domain}/dtos/requests/
    - Response DTO   → features/{domain}/dtos/responses/
    - Mapper         → features/{domain}/mappers/
    - Controller     → features/{domain}/controllers/

[ ] Sınıf ismi doğru mu?
    - Implementation "Manager" ile mi bitiyor? (Impl yasak)
    - BusinessRules sınıfı "BusinessRules" ile mi bitiyor?
    - Request DTO "Request" ile mi bitiyor?
    - Response DTO "Response" ile mi bitiyor?
    - Mapper "Mapper" ile mi bitiyor?
    - Controller çoğul ve "Controller" ile mi bitiyor?

[ ] Core'a yanlış bir şey girmemiş mi?
    - core/ altında feature'a özgü sınıf yok mu?
    - CarMapper, CarBusinessRules gibi şeyler core'da değil mi?
```

**Tespit edildiğinde:**
```
❌ Sorun: CarServiceImpl.java — "Impl" suffix kullanılmış
✅ Düzeltme: Dosya CarManager.java olarak yeniden adlandırıldı
   Neden: Bu projede implementation sınıfları "Manager" suffix'i kullanır
```

---

## Bölüm 2 — SOLID ve OOP Kontrolü

```
✅ Kontrol listesi:

[ ] @Autowired kullanılmış mı?
    → Varsa: private final + @RequiredArgsConstructor'a çevir

[ ] Field injection var mı? (private XxxService xxxService — final yok)
    → Varsa: private final yap

[ ] Constructor elle yazılmış mı?
    → Varsa: @RequiredArgsConstructor ile değiştir

[ ] Başka domain'in repository'si BusinessRules'a inject edilmiş mi?
    → CarBusinessRules içinde BrandRepository gibi
    → Varsa: O kontrolü ilgili domain'in BusinessRules'ına taşı

[ ] Interface yerine concrete sınıf inject edilmiş mi?
    → private final CarManager carManager (CarService değil)
    → Varsa: Interface tipine çevir

[ ] Bir sınıf 3'ten fazla sorumluluk taşıyor mu?
    → Başka domain'in repository'si inject ediliyorsa
    → 150 satırı geçiyorsa
    → Birbirsiz metodlar varsa
    → Varsa: Sorumlulukları ilgili sınıflara dağıt

[ ] Controller'da iş mantığı var mı?
    → if/else bloğu, hesaplama, validasyon
    → Varsa: BusinessRules veya Manager'a taşı

[ ] İç içe if bloğu 2 seviyeyi geçiyor mu?
    → Varsa: Erken return ve BusinessRules ile düzelt

[ ] Bir metod 20 satırı geçiyor mu?
    → Varsa: Private yardımcı metodlara böl
```

---

## Bölüm 3 — Spring Boot Kalıpları Kontrolü

### 3.1 Entity Kontrolü

```
✅ Kontrol listesi:

[ ] @Id alanında @GeneratedValue(strategy = GenerationType.UUID) var mı?
    → Long + IDENTITY varsa UUID + String'e çevir

[ ] ID alanı String tipinde mi?
    → Long varsa String yap

[ ] @NoArgsConstructor var mı?
    → Yoksa ekle — Hibernate zorunlu kılar

[ ] @AllArgsConstructor var mı?
    → Yoksa ekle

[ ] Enum alanlarında @Enumerated(EnumType.STRING) var mı?
    → ORDINAL varsa STRING'e çevir

[ ] İlişkilerde FetchType.EAGER kullanılmış mı?
    → Varsa LAZY'ye çevir — N+1 problemi çıkar

[ ] OneToMany ilişkisinde orphanRemoval = true var mı?
    → Yoksa ekle

[ ] Slug üretimi @PrePersist ile mi yapılıyor?
    → Manager'da manuel set ediliyorsa @PrePersist'e taşı

[ ] LocalDate kullanılmış mı?
    → Varsa LocalDateTime'a çevir — frontend ISO 8601 bekliyor
```

### 3.2 Repository Kontrolü

```
✅ Kontrol listesi:

[ ] JpaRepository<Entity, String> mi extend ediliyor?
    → İkinci parametre Long ise String yap

[ ] Basit sorgular için derived query kullanılıyor mu?
    → findBy, existsBy, countBy — @Query gereksiz yere yazılmış mı?

[ ] @Query sadece gerçekten karmaşık sorgularda mı kullanılıyor?
    → Tek filtrelik sorgu @Query ile yazılmışsa derived query'ye çevir
```

### 3.3 Manager (@Service) Kontrolü

```
✅ Kontrol listesi:

[ ] @Transactional(readOnly = true) — okuma metodlarında var mı?
    → getAll, getById, getBySlug gibi metodlarda yoksa ekle

[ ] @Transactional — yazma metodlarında var mı?
    → create, update, delete metodlarında yoksa ekle

[ ] Manager içinde iş akışı sırası doğru mu?
    → BusinessRules → Mapper → Repository → return
    → Farklı bir sıra varsa düzelt

[ ] BusinessRules çağrısı metodun başında mı?
    → Repository'den sonra çağrılıyorsa başa taşı

[ ] Manager doğrudan entity mi döndürüyor?
    → Varsa Mapper ile Response DTO'ya çevir

[ ] Manager içinde try/catch bloğu var mı?
    → Varsa kaldır — GlobalExceptionHandler halleder
```

### 3.4 Controller Kontrolü

```
✅ Kontrol listesi:

[ ] void dönen metod var mı? (DELETE hariç)
    → Varsa ResponseEntity<XxxResponse> yap

[ ] DELETE metodu ResponseEntity<Void> döndürüyor mu?
    → void ise ResponseEntity.noContent().build() yap

[ ] @Valid annotation request body'de var mı?
    → Yoksa ekle

[ ] URL /api/v1/ ile başlıyor mu?
    → Yoksa ekle

[ ] Controller'da try/catch bloğu var mı?
    → Varsa kaldır — GlobalExceptionHandler halleder

[ ] @CrossOrigin annotation var mı?
    → Varsa kaldır — SecurityConfig'de merkezi CORS var

[ ] this.xxxService şeklinde kullanım var mı?
    → Varsa this. kaldır
```

### 3.5 Mapper Kontrolü

```
✅ Kontrol listesi:

[ ] componentModel = "spring" var mı?
    → Yoksa ekle — Spring bean olarak inject edilmeli

[ ] unmappedTargetPolicy = ReportingPolicy.IGNORE var mı?
    → Yoksa ekle — eksik field derleme hatası vermemeli

[ ] ID alanı için String.valueOf() yazılmış mı?
    → Varsa kaldır — UUID String'ten String'e dönüşüm gereksiz

[ ] Update mapper'ında @BeanMapping + @MappingTarget var mı?
    → Yoksa ekle — null alanlar mevcut değeri silmemeli

[ ] Mappers.getMapper() kullanılmış mı?
    → Varsa kaldır — Spring bean injection kullan
```

### 3.6 BusinessRules Kontrolü

```
✅ Kontrol listesi:

[ ] Metod isimleri "checkIf" ile başlıyor mu?
    → validate, control gibi isimler varsa checkIf'e çevir

[ ] BusinessRules içinde repository.save() çağrısı var mı?
    → Varsa kaldır — BusinessRules sadece kontrol eder, kaydetmez

[ ] Başka domain'in repository'si inject edilmiş mi?
    → Varsa o kontrolü ilgili domain'in BusinessRules'ına taşı

[ ] Exception fırlatmak yerine boolean/Result döndürüyor mu?
    → Varsa BusinessException fırlatmaya çevir
```

---

## Bölüm 4 — Frontend API Sözleşmesi Kontrolü

```
✅ Kontrol listesi:

[ ] Response DTO'da id alanı String mi?
    → Long ise String yap

[ ] Enum alanları response'ta UPPER_CASE String olarak mı geliyor?
    → @Enumerated(EnumType.STRING) kontrol et

[ ] GetCarResponse içinde insurancePackages ve extras var mı?
    → Yoksa ekle — frontend embedded bekliyor

[ ] AuthResponse içinde user objesi var mı?
    → Sadece token dönüyorsa UserResponse ekle

[ ] grandTotal içinde deposit var mı?
    → Varsa çıkar — deposit ayrı alanda gösterilir

[ ] imageUrls List<String> mi?
    → String ise List<String>'e çevir

[ ] reservationCode "VYR-" prefix'i ile başlıyor mu?
    → CodeGenerator.generateReservationCode() kullanılıyor mu?

[ ] Tarih alanları LocalDateTime mı?
    → LocalDate varsa LocalDateTime'a çevir
```

---

## Bölüm 5 — Güvenlik Kontrolü

```
✅ Kontrol listesi:

[ ] Hassas endpoint'ler güvenli mi?
    → Admin endpoint'lerinde hasRole("ADMIN") var mı?
    → Kullanıcı endpoint'lerinde hasAnyRole("USER", "ADMIN") var mı?

[ ] Password encode ediliyor mu?
    → BCryptPasswordEncoder kullanılıyor mu?
    → Plain text password saklanıyor mu?

[ ] JWT token doğrulaması yapılıyor mu?
    → JwtFilter her istekte token kontrol ediyor mu?
```

---

## Review Raporu Formatı

Kod incelemesi sonunda her zaman bu formatta özet sun:

```
## Review Sonucu — [SınıfAdı]

### ✅ Doğru Kullanımlar
- Constructor injection doğru
- @Transactional(readOnly = true) okuma metodlarında mevcut
- UUID ile ID tanımlanmış

### ❌ Bulunan Sorunlar ve Düzeltmeler

**Sorun 1:** [Ne yanlış]
**Düzeltme:** [Nasıl düzeltildi]
**Neden:** [Hangi kural ihlal edildi]

**Sorun 2:** ...

### 📋 Özet
Toplam [N] sorun bulundu ve düzeltildi.
Kalan [N] öneri: [Varsa öneriler]
```

---

## Hızlı Referans — En Sık Yapılan Hatalar

| Hata | Kural | Düzeltme |
|------|-------|----------|
| `@Autowired` kullanımı | 02_solid | `@RequiredArgsConstructor` + `private final` |
| `*Impl` suffix | 01_architecture | `*Manager` yap |
| `Long id` entity'de | 04_api_contract | `String id` + UUID strategy |
| `FetchType.EAGER` | 03_spring | `FetchType.LAZY` yap |
| `@Enumerated(ORDINAL)` | 03_spring | `@Enumerated(STRING)` yap |
| `void` dönen controller | 03_spring | `ResponseEntity<XxxResponse>` yap |
| Controller'da try/catch | 03_spring | Kaldır, GlobalExceptionHandler var |
| Manager'da `Long.valueOf(id)` | 04_api_contract | Kaldır, UUID String zaten |
| BusinessRules'ta başka domain repo | 02_solid | O domain'in BusinessRules'ına taşı |
| `LocalDate` kullanımı | 04_api_contract | `LocalDateTime`'a çevir |
| `@CrossOrigin` controller'da | 04_api_contract | Kaldır, merkezi CORS var |
| `Mappers.getMapper()` | 03_spring | Spring injection kullan |
| `readOnly = true` eksik | 03_spring | Okuma metodlarına ekle |
| BusinessRules'ta save() çağrısı | 02_solid | Kaldır, sadece kontrol eder |
