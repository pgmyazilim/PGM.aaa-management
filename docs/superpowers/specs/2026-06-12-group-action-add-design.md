# Tasarım: Gruba İşlem (Action) Ekleme — Yetki Matrisi Formu

**Tarih:** 2026-06-12
**Durum:** Onaylandı

## Problem

Uygulamada bir kullanıcı grubuna yeni bir action (işlem) atamanın arayüzü yok.
Yetki Matrisi sayfası (`/permissions`) yalnızca grupta **zaten kayıtlı** olan
`GroupActionPermission` satırlarını listeliyor. Arka uçta
`POST /permissions/upsert` endpoint'i ve `PermissionService.upsertPermission`
metodu mevcut, ancak hiçbir şablon bunları kullanmıyor.

## Çözüm

Yetki Matrisi sayfasına, grup seçiliyken görünen bir **"Gruba İşlem Ekle"**
formu eklenir. Form, gruba henüz atanmamış action'ları listeler ve mevcut
upsert endpoint'ine gönderim yapar. Veritabanı değişikliği ve yeni endpoint
gerekmez.

## Backend Değişiklikleri

### 1. `ActionRepository` — yeni sorgu

Gruba henüz `GroupActionPermission` kaydı olmayan action'ları, modülüyle
birlikte (`JOIN FETCH` — LazyInitializationException önlenir) ada göre sıralı
getirir:

```java
@Query("SELECT a FROM Action a JOIN FETCH a.module " +
       "WHERE a.actionId NOT IN " +
       "(SELECT p.action.actionId FROM GroupActionPermission p " +
       " WHERE p.userGroup.userGroupId = :groupId) " +
       "ORDER BY a.name")
List<Action> findUnassignedForGroup(@Param("groupId") Integer groupId);
```

### 2. `PermissionService.getUnassignedActions(Integer groupId)`

Repository'ye delege eden okuma metodu.

### 3. `PermissionController.matrix`

Grup seçiliyse (`groupId != null`) modele `availableActions` özniteliği
eklenir; aksi halde boş liste.

### 4. Mevcut `upsert` endpoint'i olduğu gibi kullanılır

`<input type="datetime-local">` değeri (`yyyy-MM-ddTHH:mm`) mevcut
`LocalDateTime.parse` çağrısıyla uyumludur.

## Arayüz Değişiklikleri (`templates/permission/matrix.html`)

Filtre satırı ile matris tablosu arasına, yalnızca grup seçiliyken görünen
"Gruba İşlem Ekle" kartı:

- **İşlem** açılır menüsü: `availableActions`; etikette modül adı da gösterilir
  (örn. "Fatura Sil — Finans"); `required`.
- **İzinli** onay kutusu: varsayılan işaretli (checkbox işaretsizken değer
  göndermediği için `allowed` parametresi hidden input + checkbox düzeniyle
  veya `value` çevrimiyle güvence altına alınır).
- **Süre Sonu**: isteğe bağlı `datetime-local`.
- **İzinli Çalıştırma Sayısı**: isteğe bağlı sayı, `min=0 max=32767`
  (kolon `smallint`).
- **Ekle** butonu → `POST /permissions/upsert` (hidden `groupId` ile);
  başarıda aynı grubun matrisine geri yönlendirilir (mevcut davranış).
- Atanmamış işlem kalmadıysa form yerine kısa bilgi notu gösterilir.

## Hata Yönetimi

- `EntityNotFoundException` mevcut `GlobalExceptionHandler` tarafından
  karşılanır.
- Form doğrulaması tarayıcıda `required`/`min`/`max` öznitelikleriyle yapılır.
- Eşzamanlı ekleme çift kayıt üretmez: upsert mantığı mevcut kaydı günceller;
  `(ActionId, UserGroupId)` üzerinde unique kısıt zaten var.

## Test

- `PermissionServiceTest`'e `getUnassignedActions` için TDD ile birim test.
- Mevcut test takımı (`mvn test` / `gradle test`) tam çalıştırılır.
