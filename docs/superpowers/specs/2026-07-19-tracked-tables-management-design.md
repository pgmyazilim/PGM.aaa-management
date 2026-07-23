# Tasarım: İzlenen Tablolar (TrackedTables) Yönetim Ekranı

**Tarih:** 2026-07-19
**Durum:** Onaylandı

## Problem

AAA veritabanındaki `aaa.TrackedTables` tablosu, `RecordAudits` satır-seviyesi
denetim kayıtlarının hangi iş tablolarına yazılabileceğini belirleyen **registry**
tablosudur (bir tablo buraya `Name` ile kayıtlı değilse `RecordAudits`'e yazılamaz).
Uygulamada bu registry'yi yönetecek bir ekran yok. Mevcut `audit` paketindeki
ekranlar (Aksiyon Logları, Oturumlar, Kayıt Denetimi) salt-okunur; `TrackedTables`
yalnızca Kayıt Denetimi ekranında filtre açılır listesi olarak kullanılıyor.

## Çözüm

`audit` paketine, `TrackedTables` için **tam CRUD** (listele / ekle / düzenle / sil)
bir yönetim ekranı eklenir. Salt-okunur `AuditController`/`AuditService` ile
sorumlulukları karışmaması için ekran **ayrı, tek amaçlı** bir controller + service
ile yazılır; mevcut `TrackedTableRepository` genişletilir. Veritabanı şeması
değişmez (entity mapping mevcut ve doğru).

## Kapsam kararları (onaylandı)

- **Tam CRUD** — salt-okunur değil.
- **ActorTrackingTypes / RecordTrackingTypes** — I/U/D/S seçim kutuları; seçilenler
  birleşik string olarak saklanır.
- **Menü yeri** — sol menüdeki **Denetim** bölümüne yeni "İzlenen Tablolar" öğesi.

## Entity (mevcut — değişmez)

`com.company.aaamanagement.domain.TrackedTable` (tablo `aaa.TrackedTables`):

| Alan | Kolon | Not |
|---|---|---|
| `trackedTableId` | `TrackedTableId` | PK, IDENTITY |
| `modifiedAtUtc` | `ModifiedAtUtc` | NOT NULL, insert + her update'te `SYSUTCDATETIME()` |
| `createdAtUtc` | `CreatedAtUtc` | DB-default, read-only |
| `rowVersion` | `RowVersion` | concurrency token, read-only |
| `name` | `Name` | NOT NULL, **UNIQUE** (`UQ_TrackedTable_Name`), max 240 |
| `description` | `Description` | nvarchar(max), nullable |
| `actorTrackingTypes` | `ActorTrackingTypes` | nvarchar(8), nullable |
| `recordTrackingTypes` | `RecordTrackingTypes` | nvarchar(8), nullable |

## Backend Değişiklikleri

### 1. `TrackedTableRepository` — yeni metotlar

Mevcut `findAllByOrderByNameAsc()` korunur (Kayıt Denetimi filtresi kullanıyor).
Eklenecek:

```java
@Query("SELECT t FROM TrackedTable t WHERE :search IS NULL OR " +
       "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))")
Page<TrackedTable> findBySearch(@Param("search") String search, Pageable pageable);

boolean existsByNameIgnoreCase(String name);
boolean existsByNameIgnoreCaseAndTrackedTableIdNot(String name, Integer trackedTableId);
```

### 2. `RecordAuditRepository` — silme guard için

```java
boolean existsByTrackedTable_TrackedTableId(Integer trackedTableId);
```

### 3. `TrackedTableService` (yeni)

`@Service`, `TrackedTableRepository` + `RecordAuditRepository` bağımlılıkları.

- `listTrackedTables(String search, int page, int size)` → `Page<TrackedTable>`,
  Name'e göre sıralı.
- `findById(Integer id)` → yoksa `EntityNotFoundException`.
- `save(TrackedTable form, List<String> actorTypes, List<String> recordTypes)`:
  - `Name` boş olamaz → `IllegalArgumentException`.
  - Benzersizlik: yeni kayıtta `existsByNameIgnoreCase`, güncellemede
    `existsByNameIgnoreCaseAndTrackedTableIdNot` → çakışırsa `IllegalArgumentException`.
  - `actorTypes` / `recordTypes` listeleri `normalizeTrackingTypes(...)` ile
    **kanonik I→U→D→S sırasında** birleşik string'e çevrilir; boşsa `null`.
  - Güncellemede mevcut kayda merge edilir (form dışı alanlar korunur); yeni kayıtta
    form doğrudan kullanılır.
  - `modifiedAtUtc = LocalDateTime.now(ZoneOffset.UTC)` set edilir.
- `delete(Integer id)`:
  - `recordAuditRepository.existsByTrackedTable_TrackedTableId(id)` true ise
    `IllegalStateException` (FK ihlalini önlemek için) — referanslı tablo silinemez.
  - Aksi halde `deleteById(id)`.

`normalizeTrackingTypes(List<String>)`: gelen değerlerden yalnızca `I,U,D,S`
olanları alır, kanonik sırada tekrarsız birleştirir, sonuç boşsa `null` döner.

### 4. `TrackedTableController` (yeni) — `@RequestMapping("/audit/tracked-tables")`

| Method | Yol | Model / davranış |
|---|---|---|
| GET | `` | `trackedTables` (Page), `search`, opsiyonel `error`; `activePage="tracked-tables"` |
| GET | `/new` | boş `trackedTable`, `operationTypes` (I/U/D/S) |
| GET | `/{id}/edit` | mevcut `trackedTable`, `operationTypes` |
| POST | `/save` | `@ModelAttribute TrackedTable` + `actorTypes` / `recordTypes` `List<String>` param'ları → `service.save(...)` → redirect `/audit/tracked-tables` |

> **Not (binding çakışması):** checkbox'ların request-param adları entity alanlarından
> **farklı** tutulur (`actorTypes` / `recordTypes`), aksi halde `@ModelAttribute TrackedTable`
> aynı isimli String alanları çok-değerli param'a bağlamaya çalışıp `@RequestParam List<String>`
> ile çakışır. Entity'nin `actorTrackingTypes`/`recordTrackingTypes` alanları formdan
> bağlanmaz; yalnızca service içinde normalize edilmiş string ile set edilir.
| POST | `/{id}/delete` | `service.delete(id)` → redirect `/audit/tracked-tables` |

Validasyon/silme hataları (`IllegalArgumentException` / `IllegalStateException`)
controller'da yakalanıp `redirect:/audit/tracked-tables?error=<mesaj>` ile listeye
döndürülür; liste şablonu üstte hata bandı gösterir. (Mevcut ekranlar flash mesaj
kullanmıyor; bu ekrana özel basit bir hata bandı eklenir.)

## Şablonlar

### `templates/audit/tracked-tables.html` (liste)
- Sidebar `sidebar('tracked-tables')`.
- Üst satır: arama kutusu (`name`) + "+ Yeni Tablo" butonu.
- `error` parametresi varsa üstte kırmızı hata bandı.
- Tablo kolonları: Ad, Actor Tipleri (I/U/D/S rozetleri), Record Tipleri (rozetler),
  Son Güncelleme, İşlem (Düzenle / Sil). Boş durumda "kayıt bulunamadı" satırı.
- Sil formu `onsubmit="return confirm(...)"`.
- Stil diğer liste ekranlarıyla (ör. `infrastructure/projects.html`) uyumlu.

### `templates/audit/tracked-table-form.html` (form)
- Name (zorunlu, `maxlength="240"`), Description (textarea).
- ActorTrackingTypes: 4 checkbox — `name="actorTypes"`, `value` ∈ {I,U,D,S};
  düzenlemede entity'nin `actorTrackingTypes` string'inde geçen harfler `checked`.
- RecordTrackingTypes: aynı yapı, `name="recordTypes"` (entity `recordTrackingTypes`).
- Kaydet / İptal.

## Sidebar

`templates/layout/sidebar.html` — **Denetim** başlığı altında, Oturumlar'dan sonra:

```html
<li>
  <a th:href="@{/audit/tracked-tables}"
     th:classappend="${activePage == 'tracked-tables'} ? '...' : '...'"
     class="...">
     <span>📋</span> İzlenen Tablolar
  </a>
</li>
```

## Test

`TrackedTableServiceTest` (Mockito, mevcut `InfrastructureServiceTest` /
`UserServiceTest` stiliyle):

1. `save` — Name boş/whitespace → `IllegalArgumentException`, repo.save çağrılmaz.
2. `save` — yeni kayıtta duplike Name → `IllegalArgumentException`.
3. `save` — güncellemede aynı ismi kendi id'sinde tutmak sorun değil (çakışma yok).
4. `save` — `["D","I","U"]` gibi karışık liste → `"IUD"` kanonik string; boş liste → `null`.
5. `save` — `modifiedAtUtc` set ediliyor.
6. `delete` — referanslı tablo (RecordAudits var) → `IllegalStateException`, deleteById çağrılmaz.
7. `delete` — referanssız tablo → `deleteById` çağrılır.

## Kapsam dışı (YAGNI)

- `RowVersion` tabanlı optimistic-locking hata mesajı özelleştirmesi (mevcut
  davranış korunur).
- Silme yerine "soft delete" / arşivleme.
- Kayıt Denetimi ekranından bu ekrana çapraz link (ayrı sidebar öğesi yeterli).
