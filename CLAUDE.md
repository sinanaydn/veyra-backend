# Veyra Backend — CLAUDE.md

Bu dosya Claude Code'un her oturumda otomatik okuduğu ana konfigürasyon dosyasıdır.
Aşağıdaki kurallara **her zaman** uy — istisnası yoktur.

---

## Proje

**Veyra** — Türkiye pazarı için premium araç kiralama platformu backend API'si.  
**Stack:** Java 25 · Spring Boot 4.0.5 · PostgreSQL 17 (Docker) · Maven  
**Frontend:** Next.js 16 — `http://localhost:3000` — mock data'dan gerçek API'ye bağlanacak

---

## Skill Dosyaları — Her Zaman Oku

Bu projeye ait Java sınıfı yazarken, düzenlerken veya incelerken
aşağıdaki skill dosyalarını sırayla oku ve kurallarını uygula:

```
.claude/skills/01_veyra_architecture.md   → Paket yapısı, naming, sınıf sırası
.claude/skills/02_veyra_solid_java.md     → SOLID, OOP, injection kuralları
.claude/skills/03_veyra_spring_patterns.md → Spring Boot 4 kalıpları, JPA, Security
.claude/skills/04_veyra_api_contract.md   → Frontend sözleşmesi, endpoint, tip kuralları
.claude/skills/05_veyra_java_reviewer.md  → Kod review kontrol listesi
```

Skill dosyalarını okumadan kod yazma.
Kural çakışması olursa bu dosyadaki karar geçerlidir.

---

## Çalışma Prensibi — Adım Adım İlerle

Hiçbir zaman bir feature'ı tek seferde tamamlamaya çalışma.
Her adımı bitir, kullanıcıya göster, onay al, sonra devam et.

**Feature yazma sırası:**
```
1. Entity           → Yaz, göster, onay al
2. Repository       → Yaz, göster, onay al
3. DTO'lar          → Request ve Response, göster, onay al
4. Mapper           → Yaz, göster, onay al
5. Service (IF)     → Interface, göster, onay al
6. Manager          → Yaz, göster, onay al
7. BusinessRules    → Kullanıcıya sor: "BusinessRules oluşturalım mı?"
8. Controller       → Yaz, göster, onay al
```

`BusinessRules` sınıfını kullanıcı onayı olmadan **asla** oluşturma.

---

## Kesin Kurallar — Bunları Asla İhlal Etme

```
✗ @Autowired kullanma              → @RequiredArgsConstructor + private final
✗ *Impl suffix kullanma            → *Manager kullan
✗ Long id kullanma entity'de       → String id + GenerationType.UUID
✗ FetchType.EAGER kullanma         → FetchType.LAZY
✗ Controller'da try/catch yazma    → GlobalExceptionHandler halleder
✗ Controller'da iş mantığı yazma   → Manager ve BusinessRules'a taşı
✗ void dönen controller metodu     → ResponseEntity<XxxResponse> kullan
✗ Entity döndürme controller'dan   → Her zaman Response DTO döndür
✗ LocalDate kullanma               → LocalDateTime kullan
✗ @CrossOrigin controller'a ekleme → Merkezi CORS SecurityConfig'de var
✗ BusinessRules'a başka domain repo → O domain'in BusinessRules'ına taşı
✗ @Enumerated(ORDINAL) kullanma    → @Enumerated(STRING) kullan
```

---

## Docker — PostgreSQL Başlatma

```bash
docker compose up -d        # PostgreSQL 17'yi başlat
docker compose down         # Durdur
docker compose logs -f      # Log takibi
```

Bağlantı: `jdbc:postgresql://localhost:5432/veyra_db`  
Kullanıcı: `veyra_user` · Şifre: `veyra_pass`

---

## Uygulama Başlatma

```bash
./mvnw spring-boot:run                                    # Dev profili
./mvnw spring-boot:run -Dspring.profiles.active=dev       # Açık profil
./mvnw clean package -DskipTests                          # Build
```

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## Paket Kökü

```
com.veyra.rentacar
├── core/       → Ortak altyapı (exception, security, config, utilities)
└── features/   → Domain modülleri (cars, brands, models, auth, rentals, users, dashboard)
```
