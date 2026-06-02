# AAAManagement — Tasarım Dokümanı

**Tarih:** 2026-06-02  
**Proje:** AAAManagement  
**Amaç:** AAA (Authentication, Authorization, Auditing) veritabanını yönetmek için tek çatı altında çalışan Spring Boot MVC yönetim uygulaması.

---

## 1. Genel Bakış

AAAManagement, bir şirketin tüm yazılımlarının bağlandığı merkezi AAA sisteminin veritabanını (`[AAA]` veritabanı, `[aaa]` şeması) yönetmek için geliştirilmiş bir internal admin aracıdır. Kullanıcı kimlik doğrulaması yoktur — güvenli iç ağda çalışır. Tüm UI Türkçe'dir.

---

## 2. Teknik Stack

| Bileşen | Seçim |
|---|---|
| Dil | Java 25 |
| Framework | Spring Boot 4.0.1 |
| Build | Gradle Groovy DSL (`build.gradle`, `settings.gradle`) |
| Gradle Wrapper | 9.1.0 |
| Veritabanı | Microsoft SQL Server 2019 (v15.0.2000.5) |
| JPA / Hibernate | Spring Data JPA, Hibernate 6, Dialect: `SQLServer2012Dialect` |
| View | Spring MVC + Thymeleaf 3 |
| CSS | Tailwind CSS CDN (play mode) |
| Interaktivite | HTMX 2.x CDN |
| Utility | Lombok |
| Group ID | `com.company` |
| Artifact ID | `aaamanagement` |
| Ana Paket | `com.company.aaamanagement` |

---

## 3. Veritabanı Şeması

Veritabanı adı: `AAA`. Tüm tablolar `[aaa]` şemasındadır.

### Tablolar ve İlişkiler

```
Projects (1) ──< Modules (1) ──< Actions (1) ──< ActionConstraints (1) ──< ActionConstraintGroupValue
                                                │
                                                └──< ActionLogs
                                                └──< GroupActionPermission >── UserGroups

Users (1) ──< Sessions (1) ──< ActionLogs
Users >──< UserGroupMembers >──< UserGroups
UserGroups ──< GroupActionPermission ──> Actions
UserGroups ──< ActionConstraintGroupValue ──> ActionConstraints
UserGroups ──< SettingValues ──> Settings
Users ──< SettingValues ──> Settings

Clients >──< ClientModules >──< Modules

DatabaseServers (1) ──< DatabaseCredentials
DatabaseServers ──< ModulesDatabases ──> Modules ──> DatabaseCredentials

TrackedTables (1) ──< RecordAudits ──> Users
RecordAudits ──> Sessions
RecordAudits ──> ActionLogs

DbInfo (bağımsız — şema versiyon takibi)
```

### Kritik Kısıtlar

- `SettingValues.UserId` ve `SettingValues.UserGroupId` alanlarından tam olarak biri dolu olmalı (`CK_SettingValue_ExactlyOneTarget`)
- `ActionConstraints.Operator` yalnızca şu değerleri alabilir: `equals`, `greaterThan`, `lessThan`, `greaterThanOrEqualTo`, `lessThanOrEqualTo`, `between`, `contains`, `equalsIgnoreCase`, `startsWith`, `endsWith`, `matchesRegex`
- `ActionConstraintGroupValue.ValuesLogicalOperator` yalnızca `AND`, `OR` veya NULL olabilir
- `RecordAudits.OperationType` yalnızca `I`, `U`, `D`, `S` olabilir
- `UserGroupMembers`'da `(UserId, UserGroupId)` unique
- `GroupActionPermission`'da `(ActionId, UserGroupId)` unique
- `ActionConstraintGroupValue`'da `(ActionConstraintId, UserGroupId)` unique

---

## 4. Mimari

### 4.1 Paket Yapısı (Feature-Slice)

```
src/main/java/com/company/aaamanagement/
  ├── AAAManagementApplication.java
  ├── config/
  │   ├── WebMvcConfig.java
  │   └── GlobalExceptionHandler.java       ← @ControllerAdvice
  ├── domain/                               ← tüm @Entity sınıfları
  │   ├── User.java
  │   ├── UserGroup.java
  │   ├── UserGroupMember.java
  │   ├── Action.java
  │   ├── ActionConstraint.java
  │   ├── ActionConstraintGroupValue.java
  │   ├── ActionLog.java
  │   ├── GroupActionPermission.java
  │   ├── Module.java
  │   ├── Project.java
  │   ├── Client.java
  │   ├── ClientModule.java
  │   ├── Session.java
  │   ├── Setting.java
  │   ├── SettingValue.java
  │   ├── TrackedTable.java
  │   ├── RecordAudit.java
  │   ├── DatabaseServer.java
  │   ├── DatabaseCredential.java
  │   ├── ModuleDatabase.java
  │   └── DbInfo.java
  ├── user/
  │   ├── UserController.java
  │   ├── UserService.java
  │   └── UserRepository.java
  ├── group/
  │   ├── GroupController.java
  │   ├── GroupService.java
  │   ├── GroupRepository.java
  │   └── UserGroupMemberRepository.java
  ├── action/
  │   ├── ActionController.java
  │   ├── ActionService.java
  │   └── ActionRepository.java
  ├── permission/
  │   ├── PermissionController.java
  │   ├── PermissionService.java
  │   └── GroupActionPermissionRepository.java
  ├── constraint/
  │   ├── ConstraintController.java
  │   ├── ConstraintService.java
  │   ├── ActionConstraintRepository.java
  │   └── ActionConstraintGroupValueRepository.java
  ├── audit/
  │   ├── AuditController.java
  │   ├── AuditService.java
  │   ├── ActionLogRepository.java
  │   ├── RecordAuditRepository.java
  │   └── SessionRepository.java
  ├── setting/
  │   ├── SettingController.java
  │   ├── SettingService.java
  │   ├── SettingRepository.java
  │   └── SettingValueRepository.java
  └── infrastructure/
      ├── InfrastructureController.java
      ├── InfrastructureService.java
      ├── ProjectRepository.java
      ├── ModuleRepository.java
      ├── ClientRepository.java
      ├── ClientModuleRepository.java
      ├── DatabaseServerRepository.java
      ├── DatabaseCredentialRepository.java
      └── ModuleDatabaseRepository.java

src/main/resources/
  ├── application.yml
  └── templates/
      ├── layout/
      │   └── main.html                    ← Thymeleaf layout fragment (sidebar + container)
      ├── user/
      │   ├── list.html
      │   ├── form.html
      │   └── group-panel.html             ← HTMX sağdan kayan panel fragment
      ├── group/
      │   ├── list.html
      │   ├── form.html
      │   └── permission-matrix.html
      ├── action/
      │   ├── list.html
      │   └── form.html
      ├── constraint/
      │   ├── list.html
      │   └── form.html
      ├── audit/
      │   ├── action-logs.html
      │   ├── record-audits.html
      │   └── sessions.html
      ├── setting/
      │   ├── list.html
      │   └── form.html
      └── infrastructure/
          ├── projects.html
          ├── modules.html
          └── clients.html
```

### 4.2 Request Akışı

**Standart CRUD:**
```
Browser → Controller → Service → Repository → SQL Server [aaa schema]
                                                        ↓
Browser ← Thymeleaf template ← Model ← Service
```

**HTMX partial update (yetki toggle, kayan panel):**
```
hx-get / hx-post → Controller (fragment endpoint) → Service
                                                      ↓
hx-target div ← Thymeleaf th:fragment response
```

---

## 5. İş Kuralları (Service Katmanı)

| Kural | Uygulama Yeri |
|---|---|
| `RowVersionUtc` otomatik atama | Her `save()` öncesinde `LocalDateTime.now(ZoneOffset.UTC)` |
| Kullanıcı-Grup üyeliği çift kontrolü | `UserGroupMemberRepository.existsByUserIdAndUserGroupId()` ile kontrol, sonra `save()` |
| Grup-Yetki upsert | `GroupActionPermissionRepository.findByActionIdAndUserGroupId()` — varsa güncelle, yoksa yeni kayıt |
| `SettingValue` tek hedef kuralı | `userId != null XOR userGroupId != null` doğrulaması; `IllegalArgumentException` ile fırlat |
| `ActionConstraint` operatör doğrulama | `ConstraintOperator` enum ile doğrulama |
| `RecordAudit.OperationType` | `OperationType` enum: `I`, `U`, `D`, `S` |
| Audit loglar salt okunur | AAAManagement `ActionLogs` ve `RecordAudits`'e yalnızca okuma yapar, asla yazmaz |

---

## 6. UI Ekranları

### Navigasyon
Kalıcı sol sidebar, aktif menü öğesi Tailwind ile vurgulanır.

**Sidebar menü yapısı:**
- Kullanıcılar (`/users`)
- Gruplar (`/groups`)
- İşlemler (`/actions`)
- Yetki Matrisi (`/permissions`)
- Kısıtlar (`/constraints`)
- --- Denetim ---
- Aksiyon Logları (`/audit/action-logs`)
- Kayıt Denetimi (`/audit/record-audits`)
- Oturumlar (`/audit/sessions`)
- --- Yönetim ---
- Ayarlar (`/settings`)
- Altyapı (`/infrastructure`)

### Ekran Detayları

**Kullanıcı Listesi (`/users`)**
- Pageable tablo: Ad Soyad, Kullanıcı Adı, Aktif, Süper Kullanıcı, İşlemler
- HTMX inline arama (ad / kullanıcı adı)
- "Düzenle" → `/users/{id}/edit` (tam sayfa form)
- "Gruplar" → HTMX `hx-get="/users/{id}/groups"` → sağdan kayan panel
- Kayan panel: tüm gruplar checkbox listesi, mevcut üyelikler işaretli, "Kaydet" `hx-post`

**Grup Listesi (`/groups`)**
- Pageable tablo: Ad, Açıklama, Üye Sayısı, İşlemler
- "Düzenle" → form
- "Yetkiler" → `/permissions?groupId={id}` (yetki matrisi ekranı)

**Yetki Matrisi (`/permissions`)**
- Filtreler: Modül seçici + Grup seçici
- Tablo: İşlem Adı | Modül etiketi | İzin Var (checkbox) | Son Güncelleme | Süre Sonu
- Her checkbox değişimi: `hx-post="/permissions/toggle"` — sayfa reload yok
- İsteğe bağlı: `ExpiresAtUtc` ve `AllowedExecutionCount` için inline düzenleme

**İşlem Yönetimi (`/actions`)**
- Pageable tablo: Modül, Ad, ActionKey, Aktif, Yetkilendirme Gerekli, Log Başarı/Hata
- Yeni İşlem formu: Modül seçici, ad, açıklama, actionKey, flag'lar

**Kısıt Yönetimi (`/constraints`)**
- İşlem bazlı listeleme
- Form: İşlem seçici, ad, constraintKey, operatör dropdown (enum listesi), `isOperatorNegated`
- Grup değerleri alt tablosu (`ActionConstraintGroupValue`)

**Denetim Ekranları (Salt Okunur)**
- Aksiyon Logları: Tarih aralığı filtresi, başarı/başarısız filtresi, pageable
- Kayıt Denetimi: Tablo adı, kayıt ID, operasyon tipi filtresi, pageable
- Oturumlar: Kullanıcı filtresi, açık/kapalı filtresi, pageable

**Ayarlar (`/settings`)**
- Ayar listesi + varsayılan değer
- Ayar değerleri: Kullanıcı veya Grup bazlı değer ataması

**Altyapı (`/infrastructure`)**
- Projects CRUD
- Modules CRUD (proje seçici)
- Clients CRUD + ClientModules atama

---

## 7. Hata Yönetimi

- `@ControllerAdvice` → `GlobalExceptionHandler`
  - `DataIntegrityViolationException` → "Benzersiz kısıt ihlali" mesajı
  - `EntityNotFoundException` → 404 sayfası
  - `IllegalArgumentException` → form üzerinde inline hata mesajı
  - `Exception` → genel hata sayfası
- HTMX isteklerinde `HX-Retarget` header ile hata mesajı inline gösterilir

---

## 8. Git Commit Planı

```
git commit -m "chore: initial project setup"          ← ADIM 1: config dosyaları
git commit -m "feat: add domain entities"             ← ADIM 2: @Entity sınıfları
git commit -m "feat: add repositories"                ← ADIM 3: repository arayüzleri
git commit -m "feat: add service layer"               ← ADIM 4: service sınıfları
git commit -m "feat: add MVC controllers"             ← ADIM 5: controller sınıfları
git commit -m "feat: add thymeleaf templates"         ← ADIM 6: UI şablonları
```

Not: Commit mesajlarına `Co-authored-by` eklenmeyecek.
