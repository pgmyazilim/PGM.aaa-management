# Tasarım: Arayüzde Yerel Saat Dilimi Gösterimi

**Tarih:** 2026-08-14
**Durum:** Onaylandı

## Problem

Veritabanındaki tüm `*AtUtc` alanları (`LocalDateTime`, UTC duvar saati) arayüzde
oldukları gibi gösteriliyor — kullanıcı UTC saatini görüyor, kendi yerel saatine
çevirmek zorunda kalıyor. Bazı ekranlarda (form alanları, filtre alanları) bu
değerler aynı zamanda kullanıcıdan geri de alınıp doğrudan UTC alan olarak
bağlanıyor.

## Karar

- **Saat dilimi kaynağı:** `ZoneId.systemDefault()` (JVM/OS varsayılanı).
  Container'da `TZ` ortam değişkeni tanımlı olmadığından Dockerfile'a
  `ENV TZ=Europe/Nicosia` eklenir; aksi halde production'da systemDefault()
  UTC'den farksız kalırdı.
- **Kapsam:** Sadece salt-okunur gösterim değil; düzenlenebilir
  `datetime-local` alanları ve audit filtrelerindeki tarih aralığı da dahil.
  Aksi halde aynı ekranda bazı saatler yerel bazıları UTC görünüp kafa
  karıştırırdı.
- **Veritabanına yazılan değerler UTC kalmaya devam eder.** Dönüşüm sadece
  sunum/giriş katmanında yapılır; entity'lerin `*AtUtc` alanları ve DB şeması
  değişmez.

## Çözüm

### Yeni bileşen: `LocalTimeService`

`com.company.aaamanagement.common.LocalTimeService` — `@Component`:

```java
public LocalDateTime toLocal(LocalDateTime utc);  // UTC -> systemDefault()
public LocalDateTime toUtc(LocalDateTime local);   // systemDefault() -> UTC
```

Her iki metot da `null`'ı `null` olarak geçirir. Thymeleaf şablonlarından
Spring bean'e `@localTimeService.toLocal(...)` SpEL sözdizimiyle doğrudan
erişilir (ekstra dialect kaydı gerekmez).

### Salt-okunur gösterim (dokunulacak `#temporals.format` çağrıları)

`x` yerine `@localTimeService.toLocal(x)` sarmalanır:

- `audit/action-logs.html` — `log.occurredAtUtc`
- `audit/record-audits.html` — `r.occurredAtUtc`
- `audit/record-audit-detail.html` — `audit.occurredAtUtc`,
  `audit.actionLog.occurredAtUtc`
- `audit/sessions.html` — `openedAtUtc`, `closedAtUtc`, `expiresAtUtc`,
  `lastActivityUtc`
- `audit/tracked-tables.html` — `modifiedAtUtc`
- `infrastructure/projects.html` — `modifiedAtUtc`
- `permission/matrix.html` — `modifiedAtUtc`, `expiresAtUtc` (matris sütunları)
- `announcement/list.html` — `publishFromUtc`, `publishUntilUtc`
- `user/form.html` — `lockedUntilUtc`; "... UTC tarihine kadar" metni
  "... tarihine kadar" olarak güncellenir (artık yerel saat)

`createdAtUtc`/`modifiedAtUtc` gibi sunucu tarafından
`LocalDateTime.now(ZoneOffset.UTC)` ile set edilen, kullanıcı tarafından
düzenlenmeyen alanlar bu listeye dahil ama yazma tarafında hiç dokunulmaz.

### Düzenlenebilir `datetime-local` alanları

**`announcement/form.html`** (`publishFromUtc`, `publishUntilUtc`):
GET'te form değeri `@localTimeService.toLocal(...)` ile dolduruluyor.
`AnnouncementController.save()`'de, `@ModelAttribute` ile bağlanan
`announcement.publishFromUtc`/`publishUntilUtc` servis çağrısından önce
`localTimeService.toUtc(...)` ile UTC'ye çevrilir (entity'nin diğer alanları
etkilenmez).

**`permission/matrix.html`** (`expiresAtUtc`): input'ta ön doldurma yok (yeni
kayıt formu), şablon değişmez. `PermissionController.upsert()`'te
`LocalDateTime.parse(expiresAtUtc)` sonrası `localTimeService.toUtc(...)`
uygulanır.

### Audit filtreleri (`from`/`to`)

`action-logs`, `sessions`, `record-audits` ekranlarında kullanıcının seçtiği
tarih aralığı yerel saat olarak yorumlanır. `AuditController`'da, model'e
eklenen (ve forma geri yazılan) `from`/`to` değerleri kullanıcının girdiği
gibi **değişmeden** kalır; sorgu katmanına (`AuditService` çağrıları)
geçirilmeden hemen önce `localTimeService.toUtc(...)` ile UTC'ye çevrilir.
Böylece form her zaman kullanıcının yazdığını gösterir, ama DB karşılaştırması
doğru UTC aralığında yapılır.

### Dockerfile

`ENV TZ=Europe/Nicosia` eklenir (`FROM` satırından sonra).

## Test / Doğrulama

Bu projede otomatik test altyapısı yok (mevcut konvansiyon). Değişiklik
sonrası `./gradlew compileJava` ile derleme doğrulanır; manuel UI testi bu
oturumda yapılmıyor (server-rendered Thymeleaf, DB bağlantısı gerektiriyor).
