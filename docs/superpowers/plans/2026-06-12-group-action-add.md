# Gruba İşlem (Action) Ekleme Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Yetki Matrisi sayfasına, seçili kullanıcı grubuna henüz atanmamış action'ları ekleyebilen bir form eklemek.

**Architecture:** Mevcut `POST /permissions/upsert` endpoint'i kullanılır; tek yeni parça, gruba atanmamış action'ları getiren bir repository sorgusu + servis metodu ve `matrix.html`'e eklenen form. Veritabanı değişikliği yok.

**Tech Stack:** Spring Boot MVC, Spring Data JPA (JPQL, JOIN FETCH), Thymeleaf, Tailwind (CDN), JUnit 5 + Mockito + AssertJ, Gradle.

**Spec:** `docs/superpowers/specs/2026-06-12-group-action-add-design.md`

---

### Task 1: `getUnassignedActions` servis metodu + repository sorgusu (TDD)

**Files:**
- Modify: `src/main/java/com/company/aaamanagement/action/ActionRepository.java`
- Modify: `src/main/java/com/company/aaamanagement/permission/PermissionService.java`
- Test: `src/test/java/com/company/aaamanagement/permission/PermissionServiceTest.java`

- [ ] **Step 1: Başarısız testi yaz**

`PermissionServiceTest`'e (mevcut testlerin altına, sınıf kapanışından önce) ekle:

```java
    @Test
    void getUnassignedActions_delegatesToRepository() {
        Action unassigned = Action.builder().actionId(5).name("Fatura Sil").build();
        when(actionRepository.findUnassignedForGroup(2)).thenReturn(List.of(unassigned));

        List<Action> result = permissionService.getUnassignedActions(2);

        assertThat(result).containsExactly(unassigned);
    }
```

Dosyanın import bloğuna ekle (mevcut `java.util.Optional` importunun yanına):

```java
import java.util.List;
```

- [ ] **Step 2: Testin başarısız olduğunu doğrula**

Run: `./gradlew test --tests "com.company.aaamanagement.permission.PermissionServiceTest"`
Expected: COMPILATION FAILED — `findUnassignedForGroup` ve `getUnassignedActions` tanımlı değil.

- [ ] **Step 3: Minimal implementasyonu yaz**

`ActionRepository.java` — arayüze yeni metot (mevcut `findByModule_ModuleIdOrderByNameAsc` metodunun altına):

```java
    @Query("SELECT a FROM Action a JOIN FETCH a.module " +
           "WHERE a.actionId NOT IN " +
           "(SELECT p.action.actionId FROM GroupActionPermission p " +
           " WHERE p.userGroup.userGroupId = :groupId) " +
           "ORDER BY a.name")
    List<Action> findUnassignedForGroup(@Param("groupId") Integer groupId);
```

(`@Query`, `@Param`, `List` importları dosyada zaten var. `GroupActionPermission` JPQL içinde entity adıyla geçtiği için Java importu gerekmez.)

`PermissionService.java` — `getAllModules()` metodunun altına:

```java
    public List<Action> getUnassignedActions(Integer groupId) {
        return actionRepository.findUnassignedForGroup(groupId);
    }
```

(`Action` ve `List` importları dosyada zaten var.)

- [ ] **Step 4: Testin geçtiğini doğrula**

Run: `./gradlew test --tests "com.company.aaamanagement.permission.PermissionServiceTest"`
Expected: BUILD SUCCESSFUL, 4 test PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/aaamanagement/action/ActionRepository.java \
        src/main/java/com/company/aaamanagement/permission/PermissionService.java \
        src/test/java/com/company/aaamanagement/permission/PermissionServiceTest.java
git commit -m "feat: add query for actions not yet assigned to a group"
```

---

### Task 2: Controller — `availableActions` modeli ve checkbox uyumlu `allowed` parametresi

**Files:**
- Modify: `src/main/java/com/company/aaamanagement/permission/PermissionController.java`

- [ ] **Step 1: `matrix` metoduna `availableActions` ekle**

`PermissionController.matrix` içinde, `model.addAttribute("permissions", permissions);` satırının altına ekle:

```java
        model.addAttribute("availableActions", groupId != null
                ? permissionService.getUnassignedActions(groupId)
                : List.of());
```

- [ ] **Step 2: `upsert` metodunda `allowed` parametresine default ver**

İşaretsiz checkbox tarayıcı tarafından hiç gönderilmez; zorunlu `boolean` parametresi 400 hatasına yol açar. İmzayı değiştir:

```java
                         @RequestParam(defaultValue = "false") boolean allowed,
```

(Önceki hali: `@RequestParam boolean allowed,`)

- [ ] **Step 3: Derle ve tüm testleri çalıştır**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL, tüm testler PASS.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/company/aaamanagement/permission/PermissionController.java
git commit -m "feat: expose unassigned actions to permission matrix view"
```

---

### Task 3: `matrix.html` — "Gruba İşlem Ekle" formu

**Files:**
- Modify: `src/main/resources/templates/permission/matrix.html`

- [ ] **Step 1: Formu ekle**

Filtre formunun kapanışı (`</form>`, satır ~41) ile `<!-- Matrix Table -->` yorumu arasına ekle:

```html
        <!-- Add Action Form -->
        <div th:if="${selectedGroupId != null}" class="bg-white rounded-lg shadow p-4 mb-5">
            <h2 class="text-sm font-bold text-slate-700 mb-3">Gruba İşlem Ekle</h2>
            <form th:unless="${availableActions.empty}" method="post" th:action="@{/permissions/upsert}"
                  class="flex flex-wrap gap-3 items-end">
                <input type="hidden" name="groupId" th:value="${selectedGroupId}"/>
                <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">İşlem</label>
                    <select name="actionId" required
                            class="border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300">
                        <option value="">— İşlem seçin —</option>
                        <option th:each="a : ${availableActions}"
                                th:value="${a.actionId}"
                                th:text="${a.name + ' — ' + a.module.name}">İşlem</option>
                    </select>
                </div>
                <div class="flex items-center gap-2 pb-2">
                    <input type="checkbox" id="allowed" name="allowed" value="true" checked
                           class="w-5 h-5 accent-green-500 cursor-pointer"/>
                    <label for="allowed" class="text-sm text-slate-700">İzinli</label>
                </div>
                <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">Süre Sonu</label>
                    <input type="datetime-local" name="expiresAtUtc"
                           class="border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"/>
                </div>
                <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">İzinli Çalıştırma Sayısı</label>
                    <input type="number" name="allowedExecutionCount" min="0" max="32767"
                           class="border border-slate-300 rounded px-3 py-2 text-sm w-28 focus:outline-none focus:ring-2 focus:ring-blue-300"/>
                </div>
                <button type="submit"
                        class="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700">Ekle</button>
            </form>
            <p th:if="${availableActions.empty}" class="text-sm text-slate-400">
                Tüm işlemler bu gruba zaten atanmış.
            </p>
        </div>
```

- [ ] **Step 2: Derle ve tüm testleri çalıştır**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/templates/permission/matrix.html
git commit -m "feat: add action-to-group assignment form on permission matrix"
```

---

### Doğrulama Notları

- Uygulama `ddl-auto: validate` ile gerçek veritabanına bağlanır; canlı doğrulama için uygulamayı başlatıp `/permissions?groupId=<id>` sayfasında formu kullanın: atanmamış bir işlem seçin, "Ekle"ye basın — işlem matris tablosunda görünmeli ve açılır menüden kaybolmalıdır.
- `expiresAtUtc` boş bırakılabilir (controller boş stringi null'a çevirir); `datetime-local` değeri `yyyy-MM-ddTHH:mm` formatında gönderilir ve `LocalDateTime.parse` ile uyumludur.
