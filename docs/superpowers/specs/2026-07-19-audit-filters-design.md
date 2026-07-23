# Tasarım: Aksiyon Logları & Kayıt Denetimi — Detaylı Filtreleme

**Tarih:** 2026-07-19
**Durum:** Onaylandı

## Problem

`/audit/action-logs` ve `/audit/record-audits` ekranları temel filtrelerle
(başarı/operasyon tipi, tarih aralığı, işlem/tablo) sınırlı. Kullanıcıya göre
filtreleme, metin arama ve Aksiyon Logları'nda işlem (Action) seçimi için UI
alanı yok — `AuditService`'e zaten inject edilmiş ama kullanılmayan
`actionRepository`/`userRepository` bu amaç için mevcut. Ayrıca her iki formda
da filtre değerleri (tarih, seçili değerler) submit sonrası forma geri
yazılmıyor; kullanıcı her aramada yeniden giriyor.

## Çözüm

### Backend

**`ActionRepository`** — `findAllByOrderByNameAsc()` eklenir (mevcut
Module/Project/TrackedTable repo konvansiyonuyla aynı).

**`ActionLogRepository.findByFilters`** — imzaya `actorUserId` (Integer) ve
`search` (String) eklenir; JPQL'e:
- `(:actorUserId IS NULL OR l.actorUser.userId = :actorUserId)`
- `(:search IS NULL OR LOWER(l.userNote) LIKE LOWER(CONCAT('%',:search,'%')) OR LOWER(l.extraInfo) LIKE LOWER(CONCAT('%',:search,'%')))`

**`RecordAuditRepository.findByFilters`** — imzaya `actorUserId` ve `search`
eklenir; JPQL'e:
- `(:actorUserId IS NULL OR r.actorUser.userId = :actorUserId)`
- `(:search IS NULL OR LOWER(r.extraInfo) LIKE LOWER(CONCAT('%',:search,'%')))`

**`AuditService`**:
- `listActionLogs(Integer actionId, Integer actorUserId, Boolean success, String search, LocalDateTime from, LocalDateTime to, int page, int size)`
- `listRecordAudits(Integer tableId, Integer actorUserId, OperationType opType, String search, LocalDateTime from, LocalDateTime to, int page, int size)`
- `getAllActions()` → `actionRepository.findAllByOrderByNameAsc()`
- `getAllUsersForFilter()` → `userRepository.findAll(Sort.by("lastName", "firstName"))`
  (mevcut `AnnouncementService.getAllUsers()` deseniyle aynı)

**`AuditController`**:
- `/action-logs`: yeni `@RequestParam(required=false) Integer actorUserId`,
  `@RequestParam(required=false) String search`; model'e `allActions`,
  `allUsers`, `selectedActionId`, `selectedActorUserId`, `search`, `from`,
  `to`, `selectedSuccess` eklenir (form persistence için).
- `/record-audits`: yeni `actorUserId`, `search` param'ları; model'e
  `allUsers`, `selectedActorUserId`, `search`, `from`, `to` eklenir
  (`selectedTableId`/`selectedOpType` zaten vardı).

### Frontend

**`action-logs.html`**:
- İşlem dropdown'u (`actionId`, etiket `"${a.module.name} / ${a.name}"`,
  `th:selected` ile `selectedActionId`'ye göre).
- Kullanıcı dropdown'u (`actorUserId`, etiket `u.fullName`, `th:selected`).
- Metin arama input'u (`search`, `th:value`).
- Başarı `<select>` artık `th:selected="${selectedSuccess}"` ile korunur.
- Tarih inputları `th:value="${from != null} ? ${#temporals.format(from,'yyyy-MM-dd''T''HH:mm')} : ''"`
  deseniyle (bkz. `announcement/form.html`) korunur.

**`record-audits.html`**:
- Kullanıcı dropdown'u (aynı desen).
- Metin arama input'u.
- Tarih inputları korunur hale gelir (Tablo/Operasyon zaten korunuyordu).

Kullanıcı dropdown etiketi `username` değil `fullName` — v2 şemasında
`Username` nullable, `fullName` her zaman dolu.

## Test

`AuditService`'in mevcut hiç testi yok (önceden var olan bir boşluk, bu işin
kapsamı dışında). Yeni/genişleyen metotlar için Mockito ile repo çağrısına
doğru parametrelerin geçtiğini doğrulayan hafif bir `AuditServiceTest`
eklenir (projenin servis test kültürüyle tutarlı).

## Kapsam dışı (YAGNI)

- `Sessions` ekranına dokunulmaz.
- Kayıt Denetimi'ne `Kayıt ID` (RecordId) filtresi eklenmez.
- Yeni bir form-layout sistemi kurulmaz; mevcut `flex flex-wrap` düzeni korunur.
