# İzlenen Tablolar (TrackedTables) Yönetim Ekranı Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `aaa.TrackedTables` registry tablosu için tam CRUD (listele/ekle/düzenle/sil) bir web yönetim ekranı eklemek.

**Architecture:** Mevcut `audit` paketine, salt-okunur `AuditController`/`AuditService`'ten ayrı, tek amaçlı `TrackedTableController` + `TrackedTableService` eklenir. Mevcut `TrackedTableRepository` sorgu/benzersizlik metotlarıyla, `RecordAuditRepository` silme-guard metoduyla genişletilir. İki yeni Thymeleaf şablonu (liste + form) ve sidebar'a bir menü öğesi eklenir. Şema/entity değişmez.

**Tech Stack:** Spring Boot (Spring MVC + Spring Data JPA), Thymeleaf, Tailwind (CDN), JUnit 5 + Mockito + AssertJ, Gradle.

**Referans spec:** `docs/superpowers/specs/2026-07-19-tracked-tables-management-design.md`

---

## File Structure

- **Create** `src/main/java/com/company/aaamanagement/audit/TrackedTableService.java` — CRUD iş mantığı + tracking-type normalizasyonu + silme guard.
- **Create** `src/main/java/com/company/aaamanagement/audit/TrackedTableController.java` — `/audit/tracked-tables` altındaki 5 endpoint.
- **Create** `src/test/java/com/company/aaamanagement/audit/TrackedTableServiceTest.java` — service birim testleri (Mockito).
- **Create** `src/main/resources/templates/audit/tracked-tables.html` — liste ekranı.
- **Create** `src/main/resources/templates/audit/tracked-table-form.html` — ekle/düzenle formu.
- **Modify** `src/main/java/com/company/aaamanagement/audit/TrackedTableRepository.java` — `findBySearch` + benzersizlik metotları.
- **Modify** `src/main/java/com/company/aaamanagement/audit/RecordAuditRepository.java` — `existsByTrackedTable_TrackedTableId`.
- **Modify** `src/main/resources/templates/layout/sidebar.html` — "İzlenen Tablolar" menü öğesi.

**Genel komutlar:**
- Derleme: `./gradlew compileJava compileTestJava`
- Tek test sınıfı: `./gradlew test --tests "*TrackedTableServiceTest"`
- Tüm testler: `./gradlew test`

---

## Task 1: Repository metotları

**Files:**
- Modify: `src/main/java/com/company/aaamanagement/audit/TrackedTableRepository.java`
- Modify: `src/main/java/com/company/aaamanagement/audit/RecordAuditRepository.java`

- [ ] **Step 1: `TrackedTableRepository`'yi genişlet**

Dosyanın tam yeni hali:

```java
package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.TrackedTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrackedTableRepository extends JpaRepository<TrackedTable, Integer> {

    List<TrackedTable> findAllByOrderByNameAsc();

    @Query("SELECT t FROM TrackedTable t WHERE :search IS NULL OR " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<TrackedTable> findBySearch(@Param("search") String search, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndTrackedTableIdNot(String name, Integer trackedTableId);
}
```

- [ ] **Step 2: `RecordAuditRepository`'ye silme-guard metodu ekle**

Mevcut dosyada `interface RecordAuditRepository extends JpaRepository<RecordAudit, Long> {` gövdesinin sonuna, `findByFilters(...)` metodundan sonra şu satırı ekle:

```java
    boolean existsByTrackedTable_TrackedTableId(Integer trackedTableId);
```

- [ ] **Step 3: Derle**

Run: `./gradlew compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/aaamanagement/audit/TrackedTableRepository.java \
        src/main/java/com/company/aaamanagement/audit/RecordAuditRepository.java
git commit -m "feat(audit): add TrackedTable search/uniqueness and record-audit reference queries"
```

---

## Task 2: `TrackedTableService` (TDD)

**Files:**
- Create: `src/main/java/com/company/aaamanagement/audit/TrackedTableService.java`
- Test: `src/test/java/com/company/aaamanagement/audit/TrackedTableServiceTest.java`

- [ ] **Step 1: Failing test'i yaz**

Create `src/test/java/com/company/aaamanagement/audit/TrackedTableServiceTest.java`:

```java
package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.TrackedTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackedTableServiceTest {

    @Mock TrackedTableRepository trackedTableRepository;
    @Mock RecordAuditRepository recordAuditRepository;
    @InjectMocks TrackedTableService service;

    @Test
    void save_whenNameBlank_throwsIllegalArgument() {
        TrackedTable form = TrackedTable.builder().name("  ").build();

        assertThatThrownBy(() -> service.save(form, List.of("I"), List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ad");

        verify(trackedTableRepository, never()).save(any());
    }

    @Test
    void save_whenNewAndNameDuplicate_throwsIllegalArgument() {
        TrackedTable form = TrackedTable.builder().name("aaa.Users").build();
        when(trackedTableRepository.existsByNameIgnoreCase("aaa.Users")).thenReturn(true);

        assertThatThrownBy(() -> service.save(form, List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class);

        verify(trackedTableRepository, never()).save(any());
    }

    @Test
    void save_whenEditingSameNameOnOwnRow_isAllowed() {
        TrackedTable form = TrackedTable.builder().trackedTableId(7).name("aaa.Users").build();
        when(trackedTableRepository.existsByNameIgnoreCaseAndTrackedTableIdNot("aaa.Users", 7))
                .thenReturn(false);
        when(trackedTableRepository.findById(7)).thenReturn(Optional.of(
                TrackedTable.builder().trackedTableId(7).name("aaa.Users").build()));
        when(trackedTableRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrackedTable saved = service.save(form, List.of(), List.of());

        assertThat(saved.getTrackedTableId()).isEqualTo(7);
        verify(trackedTableRepository).save(any());
    }

    @Test
    void save_normalizesTrackingTypesToCanonicalOrder() {
        TrackedTable form = TrackedTable.builder().name("aaa.Sessions").build();
        when(trackedTableRepository.existsByNameIgnoreCase("aaa.Sessions")).thenReturn(false);
        when(trackedTableRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrackedTable saved = service.save(form, List.of("D", "I", "U"), List.of());

        assertThat(saved.getActorTrackingTypes()).isEqualTo("IUD");
        assertThat(saved.getRecordTrackingTypes()).isNull();
        assertThat(saved.getModifiedAtUtc()).isNotNull();
    }

    @Test
    void delete_whenReferencedByRecordAudits_throwsIllegalState() {
        when(recordAuditRepository.existsByTrackedTable_TrackedTableId(3)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(3))
                .isInstanceOf(IllegalStateException.class);

        verify(trackedTableRepository, never()).deleteById(any());
    }

    @Test
    void delete_whenNotReferenced_deletes() {
        when(recordAuditRepository.existsByTrackedTable_TrackedTableId(3)).thenReturn(false);

        service.delete(3);

        verify(trackedTableRepository).deleteById(3);
    }
}
```

- [ ] **Step 2: Test'in DERLENMEDEN başarısız olduğunu doğrula**

Run: `./gradlew test --tests "*TrackedTableServiceTest"`
Expected: FAIL — `TrackedTableService` henüz yok (compile error).

- [ ] **Step 3: `TrackedTableService`'i yaz**

Create `src/main/java/com/company/aaamanagement/audit/TrackedTableService.java`:

```java
package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.TrackedTable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackedTableService {

    // OperationType kanonik sırası: Insert, Update, Delete, Select
    private static final List<String> CANONICAL_TYPES = List.of("I", "U", "D", "S");

    private final TrackedTableRepository trackedTableRepository;
    private final RecordAuditRepository recordAuditRepository;

    public Page<TrackedTable> listTrackedTables(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return trackedTableRepository.findBySearch(search, pageable);
    }

    public TrackedTable findById(Integer id) {
        return trackedTableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("İzlenen tablo bulunamadı: " + id));
    }

    @Transactional
    public TrackedTable save(TrackedTable form, List<String> actorTypes, List<String> recordTypes) {
        if (form.getName() == null || form.getName().isBlank()) {
            throw new IllegalArgumentException("Tablo adı boş olamaz.");
        }
        String name = form.getName().trim();
        boolean isNew = form.getTrackedTableId() == null;
        boolean duplicate = isNew
                ? trackedTableRepository.existsByNameIgnoreCase(name)
                : trackedTableRepository.existsByNameIgnoreCaseAndTrackedTableIdNot(name, form.getTrackedTableId());
        if (duplicate) {
            throw new IllegalArgumentException("Bu tablo adı zaten kayıtlı: " + name);
        }

        TrackedTable entity = isNew ? form : mergeIntoExisting(form);
        entity.setName(name);
        entity.setDescription(blankToNull(form.getDescription()));
        entity.setActorTrackingTypes(normalizeTrackingTypes(actorTypes));
        entity.setRecordTrackingTypes(normalizeTrackingTypes(recordTypes));
        entity.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return trackedTableRepository.save(entity);
    }

    @Transactional
    public void delete(Integer id) {
        if (recordAuditRepository.existsByTrackedTable_TrackedTableId(id)) {
            throw new IllegalStateException(
                    "Bu tabloya bağlı denetim kayıtları olduğu için silinemez.");
        }
        trackedTableRepository.deleteById(id);
    }

    // form yalnızca düzenlenebilir alanları taşır; CreatedAtUtc/RowVersion gibi
    // form dışı alanlar mevcut kayıttan korunur.
    private TrackedTable mergeIntoExisting(TrackedTable form) {
        return findById(form.getTrackedTableId());
    }

    private String normalizeTrackingTypes(List<String> selected) {
        if (selected == null || selected.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String type : CANONICAL_TYPES) {
            if (selected.contains(type)) {
                sb.append(type);
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
```

- [ ] **Step 4: Testlerin geçtiğini doğrula**

Run: `./gradlew test --tests "*TrackedTableServiceTest"`
Expected: PASS (6 test)

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/aaamanagement/audit/TrackedTableService.java \
        src/test/java/com/company/aaamanagement/audit/TrackedTableServiceTest.java
git commit -m "feat(audit): add TrackedTableService with CRUD, type normalization, delete guard"
```

---

## Task 3: `TrackedTableController`

**Files:**
- Create: `src/main/java/com/company/aaamanagement/audit/TrackedTableController.java`

- [ ] **Step 1: Controller'ı yaz**

Create `src/main/java/com/company/aaamanagement/audit/TrackedTableController.java`:

```java
package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.TrackedTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/audit/tracked-tables")
@RequiredArgsConstructor
public class TrackedTableController {

    private final TrackedTableService trackedTableService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) String error,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        model.addAttribute("trackedTables", trackedTableService.listTrackedTables(search, page, size));
        model.addAttribute("search", search);
        model.addAttribute("error", error);
        model.addAttribute("activePage", "tracked-tables");
        return "audit/tracked-tables";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("trackedTable", new TrackedTable());
        model.addAttribute("operationTypes", OperationType.values());
        model.addAttribute("activePage", "tracked-tables");
        return "audit/tracked-table-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("trackedTable", trackedTableService.findById(id));
        model.addAttribute("operationTypes", OperationType.values());
        model.addAttribute("activePage", "tracked-tables");
        return "audit/tracked-table-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute TrackedTable trackedTable,
                       @RequestParam(required = false) List<String> actorTypes,
                       @RequestParam(required = false) List<String> recordTypes) {
        try {
            trackedTableService.save(trackedTable, actorTypes, recordTypes);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return redirectWithError(e.getMessage());
        }
        return "redirect:/audit/tracked-tables";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        try {
            trackedTableService.delete(id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return redirectWithError(e.getMessage());
        }
        return "redirect:/audit/tracked-tables";
    }

    private String redirectWithError(String message) {
        String encoded = UriComponentsBuilder.fromPath("/audit/tracked-tables")
                .queryParam("error", message)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
        return "redirect:" + encoded;
    }
}
```

- [ ] **Step 2: Derle**

Run: `./gradlew compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/aaamanagement/audit/TrackedTableController.java
git commit -m "feat(audit): add TrackedTableController CRUD endpoints"
```

---

## Task 4: Liste şablonu

**Files:**
- Create: `src/main/resources/templates/audit/tracked-tables.html`

- [ ] **Step 1: Şablonu yaz**

Create `src/main/resources/templates/audit/tracked-tables.html`:

```html
<!DOCTYPE html>
<html lang="tr" xmlns:th="http://www.thymeleaf.org">
<head><meta charset="UTF-8"><title>İzlenen Tablolar - AAA Yönetim</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="bg-gray-50">
<div class="flex h-screen">
    <div th:replace="~{layout/sidebar :: sidebar('tracked-tables')}"></div>
    <main class="flex-1 overflow-y-auto p-6">
        <div class="flex items-center gap-4 mb-5">
            <h1 class="text-xl font-bold text-slate-800">İzlenen Tablolar</h1>
            <div class="ml-auto">
                <a th:href="@{/audit/tracked-tables/new}"
                   class="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700">+ Yeni Tablo</a>
            </div>
        </div>
        <div th:if="${error}" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded text-sm"
             th:text="${error}">Hata</div>
        <form method="get" th:action="@{/audit/tracked-tables}" class="mb-4 flex gap-2">
            <input type="text" name="search" th:value="${search}" placeholder="Tablo adı..."
                   class="border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none w-72"/>
            <button type="submit" class="bg-slate-700 text-white px-4 py-2 rounded text-sm hover:bg-slate-800">Ara</button>
        </form>
        <div class="bg-white rounded-lg shadow overflow-hidden">
            <table class="w-full text-sm">
                <thead class="bg-slate-800 text-white">
                <tr>
                    <th class="px-4 py-3 text-left">Tablo Adı</th>
                    <th class="px-4 py-3 text-left">Actor Tipleri</th>
                    <th class="px-4 py-3 text-left">Record Tipleri</th>
                    <th class="px-4 py-3 text-left">Son Güncelleme</th>
                    <th class="px-4 py-3 text-center">İşlem</th>
                </tr>
                </thead>
                <tbody>
                <tr th:each="t, stat : ${trackedTables.content}"
                    th:classappend="${stat.odd} ? 'bg-white' : 'bg-slate-50'"
                    class="border-b border-slate-100">
                    <td class="px-4 py-2 font-mono text-sm" th:text="${t.name}">tablo</td>
                    <td class="px-4 py-2 text-xs text-slate-500 font-mono" th:text="${t.actorTrackingTypes} ?: '—'">—</td>
                    <td class="px-4 py-2 text-xs text-slate-500 font-mono" th:text="${t.recordTrackingTypes} ?: '—'">—</td>
                    <td class="px-4 py-2 text-xs text-slate-500"
                        th:text="${#temporals.format(t.modifiedAtUtc, 'dd.MM.yyyy HH:mm')}">—</td>
                    <td class="px-4 py-2 text-center space-x-2">
                        <a th:href="@{/audit/tracked-tables/{id}/edit(id=${t.trackedTableId})}"
                           class="text-blue-600 hover:underline text-xs">Düzenle</a>
                        <form th:action="@{/audit/tracked-tables/{id}/delete(id=${t.trackedTableId})}" method="post"
                              class="inline" onsubmit="return confirm('Silinsin mi?')">
                            <button type="submit" class="text-red-500 hover:underline text-xs">Sil</button>
                        </form>
                    </td>
                </tr>
                <tr th:if="${trackedTables.content.empty}">
                    <td colspan="5" class="px-4 py-8 text-center text-slate-400">Kayıt bulunamadı.</td>
                </tr>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/audit/tracked-tables.html
git commit -m "feat(audit): add tracked-tables list template"
```

---

## Task 5: Form şablonu

**Files:**
- Create: `src/main/resources/templates/audit/tracked-table-form.html`

- [ ] **Step 1: Şablonu yaz**

`operationTypes` model niteliği `OperationType` enum değerleridir (I/U/D/S); her biri için bir checkbox üretilir ve `t.name()` ilgili tracking-type string'inde geçiyorsa işaretlenir. Create `src/main/resources/templates/audit/tracked-table-form.html`:

```html
<!DOCTYPE html>
<html lang="tr" xmlns:th="http://www.thymeleaf.org">
<head><meta charset="UTF-8"><title>İzlenen Tablo Formu - AAA Yönetim</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="bg-gray-50">
<div class="flex h-screen">
    <div th:replace="~{layout/sidebar :: sidebar('tracked-tables')}"></div>
    <main class="flex-1 overflow-y-auto p-6">
        <div class="max-w-2xl mx-auto">
            <div class="flex items-center gap-3 mb-6">
                <a th:href="@{/audit/tracked-tables}" class="text-slate-500 hover:text-slate-700 text-sm">← İzlenen Tablolar</a>
                <h1 class="text-xl font-bold text-slate-800"
                    th:text="${trackedTable.trackedTableId != null} ? 'İzlenen Tablo Düzenle' : 'Yeni İzlenen Tablo'">Tablo</h1>
            </div>
            <form th:action="@{/audit/tracked-tables/save}" method="post"
                  class="bg-white rounded-lg shadow p-6 space-y-4">
                <input type="hidden" name="trackedTableId" th:value="${trackedTable.trackedTableId}"/>
                <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">Tablo Adı *</label>
                    <input type="text" name="name" th:value="${trackedTable.name}" required maxlength="240"
                           class="w-full border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"/>
                    <p class="text-xs text-slate-400 mt-1">Denetlenecek iş tablosunun adı (benzersiz).</p>
                </div>
                <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">Açıklama</label>
                    <textarea name="description" rows="3" th:text="${trackedTable.description}"
                              class="w-full border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"></textarea>
                </div>
                <div class="grid grid-cols-2 gap-6">
                    <div>
                        <label class="block text-xs font-semibold text-slate-600 mb-2">Actor Tipleri</label>
                        <div class="space-y-1">
                            <label th:each="op : ${operationTypes}" class="flex items-center gap-2 text-sm cursor-pointer">
                                <input type="checkbox" name="actorTypes" th:value="${op.name()}"
                                       th:checked="${trackedTable.actorTrackingTypes != null && #strings.contains(trackedTable.actorTrackingTypes, op.name())}"
                                       class="w-4 h-4"/>
                                <span th:text="${op.name()}">I</span>
                            </label>
                        </div>
                    </div>
                    <div>
                        <label class="block text-xs font-semibold text-slate-600 mb-2">Record Tipleri</label>
                        <div class="space-y-1">
                            <label th:each="op : ${operationTypes}" class="flex items-center gap-2 text-sm cursor-pointer">
                                <input type="checkbox" name="recordTypes" th:value="${op.name()}"
                                       th:checked="${trackedTable.recordTrackingTypes != null && #strings.contains(trackedTable.recordTrackingTypes, op.name())}"
                                       class="w-4 h-4"/>
                                <span th:text="${op.name()}">I</span>
                            </label>
                        </div>
                    </div>
                </div>
                <p class="text-xs text-slate-400">I = Insert, U = Update, D = Delete, S = Select</p>
                <div class="flex gap-3">
                    <button type="submit" class="bg-blue-600 text-white px-6 py-2 rounded text-sm hover:bg-blue-700">Kaydet</button>
                    <a th:href="@{/audit/tracked-tables}" class="px-6 py-2 rounded text-sm border border-slate-300 hover:bg-slate-50">İptal</a>
                </div>
            </form>
        </div>
    </main>
</div>
</body>
</html>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/audit/tracked-table-form.html
git commit -m "feat(audit): add tracked-table create/edit form template"
```

---

## Task 6: Sidebar menü öğesi

**Files:**
- Modify: `src/main/resources/templates/layout/sidebar.html`

- [ ] **Step 1: "İzlenen Tablolar" öğesini ekle**

`src/main/resources/templates/layout/sidebar.html` içinde Oturumlar `<li>` bloğunu bul:

```html
        <li>
            <a th:href="@{/audit/sessions}"
               th:classappend="${activePage == 'sessions'} ? 'bg-slate-700 text-white' : 'hover:bg-slate-700 hover:text-white'"
               class="flex items-center gap-2 px-4 py-2 transition-colors">
                <span>🖥</span> Oturumlar
            </a>
        </li>
```

Bu bloğun hemen ARDINDAN şu `<li>` bloğunu ekle:

```html
        <li>
            <a th:href="@{/audit/tracked-tables}"
               th:classappend="${activePage == 'tracked-tables'} ? 'bg-slate-700 text-white' : 'hover:bg-slate-700 hover:text-white'"
               class="flex items-center gap-2 px-4 py-2 transition-colors">
                <span>📋</span> İzlenen Tablolar
            </a>
        </li>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/layout/sidebar.html
git commit -m "feat(audit): add tracked-tables menu item to sidebar"
```

---

## Task 7: Tam doğrulama

**Files:** (yok — sadece doğrulama)

- [ ] **Step 1: Tüm testleri ve derlemeyi çalıştır**

Run: `./gradlew compileJava compileTestJava test`
Expected: BUILD SUCCESSFUL, tüm testler geçer (TrackedTableServiceTest dahil).

- [ ] **Step 2: Uygulamayı elle doğrula (canlı DB gerektirir)**

Not: Bu adım çalışan bir SQL Server bağlantısı gerektirir. Uygulama ayağa kalkarsa:
- Sidebar → Denetim → "İzlenen Tablolar" ekranı açılıyor mu?
- "+ Yeni Tablo" ile kayıt eklenip listede görünüyor mu (Actor/Record tipleri rozetleri doğru)?
- Düzenle: checkbox'lar mevcut değerlerle işaretli geliyor mu, değişiklik kaydediliyor mu?
- Aynı isimle ikinci kayıt → üstte kırmızı hata bandı.
- `RecordAudits` referansı olan bir tabloyu silmeye çalışmak → hata bandı ("denetim kayıtları olduğu için silinemez").
- Referansı olmayan tabloyu sil → listeden kalkıyor.

Canlı DB yoksa bu adımı atla ve durumu (doğrulanmadı) olarak raporla.

- [ ] **Step 3: (varsa) doğrulama sonrası commit yok**

Doğrulama commit gerektirmez.

---

## Notlar

- **Commit politikası:** Bu oturumda kullanıcı commit konusunda ihtiyatlı; planı uygularken commit adımlarını çalıştırmadan önce kullanıcı onayını doğrula (veya kullanıcı "commit etme" derse commit adımlarını atla, değişiklikleri working tree'de bırak).
- **Şema değişmez:** `TrackedTable` entity ve `aaa.TrackedTables` DDL mevcut haliyle doğru; `ddl-auto: validate` ile uyumlu.
- **Konvansiyonlar (aaa-database):** `ModifiedAtUtc` her yazımda UTC set edilir; `CreatedAtUtc`/`RowVersion` read-only; `Name` benzersizdir.
```
