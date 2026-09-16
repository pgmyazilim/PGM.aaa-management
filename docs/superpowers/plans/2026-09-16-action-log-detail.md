# Aksiyon Kayıtları Detay Ekranı Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a detail page for `ActionLog` (`/audit/action-logs/{id}`) that shows the full `UserNote` (plain text) and `ExtraInfo` (JSON-or-plain-text, same pattern as `RecordAudit.recordValues`) fields, and make the Aksiyon Kayıtları list rows navigate to it.

**Architecture:** Mirror the existing `RecordAudit` detail vertical exactly: a new `AuditService.findActionLogById`, a new `AuditController` GET endpoint, a new Thymeleaf template copying the JSON-parse-with-fallback script from `record-audit-detail.html`, and a `data-href` + click-JS addition to the list template.

**Tech Stack:** Spring Boot (Java), Thymeleaf, JUnit 5 + Mockito + AssertJ, vanilla JS (no build step, Tailwind via CDN).

---

### Task 1: `AuditService.findActionLogById`

**Files:**
- Modify: `src/main/java/com/company/aaamanagement/audit/AuditService.java`
- Test: `src/test/java/com/company/aaamanagement/audit/AuditServiceTest.java`

- [ ] **Step 1: Write the failing tests**

Add to `AuditServiceTest.java` (after `findRecordAuditById_whenMissing_throwsEntityNotFound`):

```java
    @Test
    void findActionLogById_whenFound_returnsIt() {
        ActionLog log = ActionLog.builder().actionLogId(5L).build();
        when(actionLogRepository.findById(5L)).thenReturn(Optional.of(log));

        assertThat(service.findActionLogById(5L)).isSameAs(log);
    }

    @Test
    void findActionLogById_whenMissing_throwsEntityNotFound() {
        when(actionLogRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findActionLogById(9L))
                .isInstanceOf(EntityNotFoundException.class);
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "com.company.aaamanagement.audit.AuditServiceTest"`
Expected: FAIL — `findActionLogById` does not exist on `AuditService` (compile error).

- [ ] **Step 3: Implement `findActionLogById`**

In `AuditService.java`, add after `findRecordAuditById`:

```java
    public ActionLog findActionLogById(Long id) {
        return actionLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aksiyon logu bulunamadı: " + id));
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "com.company.aaamanagement.audit.AuditServiceTest"`
Expected: PASS (all tests in the class, including the 2 new ones).

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/company/aaamanagement/audit/AuditService.java src/test/java/com/company/aaamanagement/audit/AuditServiceTest.java
git commit -m "Add AuditService.findActionLogById"
```

---

### Task 2: `AuditController` detail endpoint

**Files:**
- Modify: `src/main/java/com/company/aaamanagement/audit/AuditController.java`

There's no existing dedicated controller test file for `AuditController` (the `record-audits/{id}` endpoint added earlier has no controller-level test either — it's covered by the template-rendering test in Task 3). Follow the same pattern: no new test file here, just the endpoint. Task 3's template rendering test exercises the model attributes this endpoint must supply (`log`, `activePage`).

- [ ] **Step 1: Add the endpoint**

In `AuditController.java`, add after `recordAuditDetail`:

```java
    @GetMapping("/action-logs/{id}")
    public String actionLogDetail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("log", auditService.findActionLogById(id));
        model.addAttribute("activePage", "action-logs");
        return "audit/action-log-detail";
    }
```

- [ ] **Step 2: Compile to verify no errors**

Run: `./gradlew compileJava`
Expected: BUILD SUCCESSFUL (the referenced template `audit/action-log-detail` doesn't exist yet — that's fine, it's only resolved at request time, not compile time).

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/company/aaamanagement/audit/AuditController.java
git commit -m "Add /audit/action-logs/{id} detail endpoint"
```

---

### Task 3: `action-log-detail.html` template

**Files:**
- Create: `src/main/resources/templates/audit/action-log-detail.html`
- Test: `src/test/java/com/company/aaamanagement/audit/AuditTemplateRenderingTest.java`

- [ ] **Step 1: Write the failing test**

Add to `AuditTemplateRenderingTest.java` (after `recordAuditDetailTemplate_parsesAndRendersWithAndWithoutActionLog`):

```java
    @Test
    void actionLogDetailTemplate_parsesAndRendersJsonAndPlainExtraInfo() {
        User actor = user(3, "Alan", "Turing");
        Module module = Module.builder().moduleId(1).name("Kimlik").build();
        Action action = Action.builder().actionId(1).module(module).name("Kullanıcı Güncelle").build();

        ActionLog withJsonExtraInfo = ActionLog.builder()
                .actionLogId(10L)
                .action(action)
                .actorUser(actor)
                .success(true)
                .occurredAtUtc(LocalDateTime.now())
                .userNote("test notu")
                .extraInfo("{\"foo\":\"bar\"}")
                .build();

        String outJson = buildEngine().process("audit/action-log-detail",
                realWebContext(Map.of("log", withJsonExtraInfo, "activePage", "action-logs")));
        assertThat(outJson).contains("test notu");
        assertThat(outJson).contains("{&quot;foo&quot;:&quot;bar&quot;}");
        assertThat(outJson).contains("id=\"extraInfoFormatted\"");
        assertThat(outJson).contains("id=\"extraInfoRaw\"");
        assertThat(outJson).contains("id=\"toggleRawExtraInfo\"");

        ActionLog withPlainExtraInfo = ActionLog.builder()
                .actionLogId(11L)
                .action(action)
                .success(false)
                .occurredAtUtc(LocalDateTime.now())
                .extraInfo("düz metin ek bilgi")
                .build();

        String outPlain = buildEngine().process("audit/action-log-detail",
                realWebContext(Map.of("log", withPlainExtraInfo, "activePage", "action-logs")));
        assertThat(outPlain).contains("düz metin ek bilgi");
        assertThat(outPlain).contains("Not yok");

        ActionLog withoutExtraInfo = ActionLog.builder()
                .actionLogId(12L)
                .action(action)
                .success(true)
                .occurredAtUtc(LocalDateTime.now())
                .build();

        String outEmpty = buildEngine().process("audit/action-log-detail",
                realWebContext(Map.of("log", withoutExtraInfo, "activePage", "action-logs")));
        assertThat(outEmpty).contains("Ek bilgi yok");
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "com.company.aaamanagement.audit.AuditTemplateRenderingTest"`
Expected: FAIL — template `audit/action-log-detail` not found.

- [ ] **Step 3: Create the template**

Create `src/main/resources/templates/audit/action-log-detail.html`:

```html
<!DOCTYPE html>
<html lang="tr" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Aksiyon Logu Detayı - AAA Yönetim</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50">
<div class="flex h-screen">
    <div th:replace="~{layout/sidebar :: sidebar('action-logs')}"></div>
    <main class="flex-1 overflow-y-auto p-6">
        <div class="max-w-3xl mx-auto space-y-6">
            <div class="flex items-center gap-3">
                <a th:href="@{/audit/action-logs}" class="text-slate-500 hover:text-slate-700 text-sm">← Aksiyon Logları</a>
                <h1 class="text-xl font-bold text-slate-800">Aksiyon Logu Detayı</h1>
            </div>

            <div class="bg-white rounded-lg shadow p-6">
                <h2 class="font-semibold text-slate-700 mb-4">Genel Bilgiler</h2>
                <dl class="grid grid-cols-2 gap-4 text-sm">
                    <div>
                        <dt class="text-xs font-semibold text-slate-500">İşlem</dt>
                        <dd th:text="${log.action.module.name} + ' / ' + ${log.action.name}">İşlem</dd>
                    </div>
                    <div>
                        <dt class="text-xs font-semibold text-slate-500">Kullanıcı</dt>
                        <dd th:text="${log.actorUser != null} ? ${log.actorUser.fullName} : '—'">Kullanıcı</dd>
                    </div>
                    <div>
                        <dt class="text-xs font-semibold text-slate-500">Sonuç</dt>
                        <dd>
                            <span th:if="${log.success}" class="text-green-600 font-bold text-xs">✓ Başarılı</span>
                            <span th:unless="${log.success}" class="text-red-500 font-bold text-xs">✗ Başarısız</span>
                        </dd>
                    </div>
                    <div>
                        <dt class="text-xs font-semibold text-slate-500">Oturum</dt>
                        <dd class="font-mono text-xs text-slate-500"
                            th:text="${log.session != null} ? ${log.session.sessionKey} : '—'">—</dd>
                    </div>
                    <div>
                        <dt class="text-xs font-semibold text-slate-500">Tarih</dt>
                        <dd th:text="${#temporals.format(@localTimeService.toLocal(log.occurredAtUtc), 'dd.MM.yyyy HH:mm:ss')}">—</dd>
                    </div>
                </dl>
            </div>

            <div class="bg-white rounded-lg shadow p-6">
                <h2 class="font-semibold text-slate-700 mb-2">Kullanıcı Notu</h2>
                <p th:if="${log.userNote != null}" class="text-sm whitespace-pre-wrap" th:text="${log.userNote}">—</p>
                <p th:if="${log.userNote == null}" class="text-sm text-slate-400">— Not yok —</p>
            </div>

            <div class="bg-white rounded-lg shadow p-6">
                <div class="flex items-center justify-between mb-2">
                    <h2 class="font-semibold text-slate-700">Ek Bilgi</h2>
                    <button type="button" id="toggleRawExtraInfo"
                            th:if="${log.extraInfo != null}"
                            class="hidden text-xs text-slate-500 hover:text-slate-700 underline">Ham JSON göster</button>
                </div>
                <div th:if="${log.extraInfo != null}">
                    <div id="extraInfoFormatted" class="text-sm text-slate-600"></div>
                    <pre id="extraInfoRaw"
                         class="bg-slate-50 border border-slate-200 rounded p-3 text-xs overflow-x-auto whitespace-pre-wrap"
                         th:text="${log.extraInfo}">{}</pre>
                </div>
                <p th:if="${log.extraInfo == null}" class="text-sm text-slate-400">— Ek bilgi yok —</p>
            </div>
        </div>
    </main>
</div>

<script th:inline="none">
    // "Ek Bilgi" içindeki JSON'u okunabilir bir tabloya dönüştürür.
    // JS kapalıysa ya da geçerli JSON değilse ham metin <pre> bloğu olduğu gibi görünmeye devam eder.
    (function () {
        var raw = document.getElementById('extraInfoRaw');
        var formatted = document.getElementById('extraInfoFormatted');
        var toggle = document.getElementById('toggleRawExtraInfo');
        if (!raw || !formatted) return;

        var data;
        try {
            data = JSON.parse(raw.textContent.trim());
        } catch (e) {
            // Geçerli JSON değil: ham metni olduğu gibi bırak.
            return;
        }

        formatted.innerHTML = render(data);
        raw.textContent = JSON.stringify(data, null, 2);
        raw.classList.add('hidden');

        if (toggle) {
            toggle.classList.remove('hidden');
            toggle.addEventListener('click', function () {
                var rawHidden = raw.classList.toggle('hidden');
                formatted.classList.toggle('hidden', !rawHidden);
                toggle.textContent = rawHidden ? 'Ham JSON göster' : 'Ham JSON gizle';
            });
        }

        function esc(s) {
            return String(s).replace(/[&<>"]/g, function (c) {
                return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c];
            });
        }

        function isScalar(v) {
            return v === null || typeof v !== 'object';
        }

        function scalar(v) {
            if (v === null) return '<span class="text-slate-400 italic">yok</span>';
            if (typeof v === 'boolean') return '<span class="font-mono text-indigo-600">' + (v ? 'evet' : 'hayır') + '</span>';
            if (typeof v === 'number') return '<span class="font-mono text-slate-700">' + v + '</span>';
            if (v === '') return '<span class="text-slate-400 italic">boş</span>';
            return '<span class="text-slate-800">' + esc(v) + '</span>';
        }

        function render(value) {
            if (value && typeof value === 'object' && Array.isArray(value.changes)) {
                return changesTable(value.changes);
            }
            if (Array.isArray(value)) return list(value);
            if (value && typeof value === 'object') return kvTable(value);
            return '<div>' + scalar(value) + '</div>';
        }

        function changesTable(changes) {
            if (!changes.length) return '<p class="text-slate-400">— Değişiklik kaydı yok —</p>';
            var rows = changes.map(function (c) {
                return '<tr class="border-t border-slate-100 align-top">' +
                    '<td class="py-1.5 pr-4 font-medium text-slate-600 whitespace-nowrap">' + esc(c.field) + '</td>' +
                    '<td class="py-1.5 pr-4 text-rose-600 line-through">' + scalar(c.old) + '</td>' +
                    '<td class="py-1.5 text-emerald-700">' + scalar(c.new) + '</td>' +
                    '</tr>';
            }).join('');
            return '<table class="w-full">' +
                '<thead><tr class="text-xs font-semibold text-slate-400 text-left">' +
                '<th class="pb-1 pr-4">Alan</th><th class="pb-1 pr-4">Eski Değer</th><th class="pb-1">Yeni Değer</th>' +
                '</tr></thead><tbody>' + rows + '</tbody></table>';
        }

        function kvTable(obj) {
            var keys = Object.keys(obj);
            if (!keys.length) return '<p class="text-slate-400">— Boş —</p>';
            var rows = keys.map(function (k) {
                var v = obj[k];
                var cell = isScalar(v) ? scalar(v) : '<div class="mt-1">' + render(v) + '</div>';
                return '<tr class="border-t border-slate-100 align-top">' +
                    '<td class="py-1.5 pr-4 font-medium text-slate-600 whitespace-nowrap">' + esc(k) + '</td>' +
                    '<td class="py-1.5 w-full">' + cell + '</td>' +
                    '</tr>';
            }).join('');
            return '<table class="w-full"><tbody>' + rows + '</tbody></table>';
        }

        function list(arr) {
            if (!arr.length) return '<p class="text-slate-400">— Boş liste —</p>';
            if (arr.every(isScalar)) {
                return '<ul class="list-disc list-inside space-y-0.5">' +
                    arr.map(function (v) { return '<li>' + scalar(v) + '</li>'; }).join('') + '</ul>';
            }
            return '<div class="space-y-2">' + arr.map(function (v, i) {
                return '<div class="border border-slate-200 rounded p-2">' +
                    '<div class="text-xs font-semibold text-slate-400 mb-1">#' + (i + 1) + '</div>' +
                    render(v) + '</div>';
            }).join('') + '</div>';
        }
    })();
</script>
</body>
</html>
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "com.company.aaamanagement.audit.AuditTemplateRenderingTest"`
Expected: PASS (all tests in the class, including the new one).

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/templates/audit/action-log-detail.html src/test/java/com/company/aaamanagement/audit/AuditTemplateRenderingTest.java
git commit -m "Add action-log-detail template with JSON-or-plain-text ExtraInfo rendering"
```

---

### Task 4: Make Aksiyon Kayıtları list rows clickable

**Files:**
- Modify: `src/main/resources/templates/audit/action-logs.html`
- Test: `src/test/java/com/company/aaamanagement/audit/AuditTemplateRenderingTest.java`

- [ ] **Step 1: Write the failing test**

Add to `AuditTemplateRenderingTest.java` (after `actionLogDetailTemplate_parsesAndRendersJsonAndPlainExtraInfo`):

```java
    @Test
    void actionLogsListTemplate_parsesAndRendersRowsWithDataHref() {
        User actor = user(4, "Ada", "Lovelace");
        Module module = Module.builder().moduleId(1).name("Kimlik").build();
        Action action = Action.builder().actionId(1).module(module).name("Giriş").build();
        ActionLog row = ActionLog.builder()
                .actionLogId(77L)
                .action(action)
                .actorUser(actor)
                .success(true)
                .occurredAtUtc(LocalDateTime.now())
                .build();

        Map<String, Object> vars = Map.of(
                "logs", new PageImpl<>(List.of(row), PageRequest.of(0, 50), 1),
                "allActions", List.of(action),
                "allUsers", List.of(actor),
                "activePage", "action-logs"
        );

        String out = buildEngine().process("audit/action-logs", realWebContext(vars));

        assertThat(out).contains("data-href=\"/audit/action-logs/77\"");
        assertThat(out).doesNotContain("onclick");
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "com.company.aaamanagement.audit.AuditTemplateRenderingTest"`
Expected: FAIL — output does not contain `data-href="/audit/action-logs/77"`.

- [ ] **Step 3: Add `data-href` and click handler to `action-logs.html`**

In `action-logs.html`, change the row `<tr>` (currently lines 58-60):

```html
                <tr th:each="log, stat : ${logs.content}"
                    th:classappend="${stat.odd} ? 'bg-white' : 'bg-slate-50'"
                    class="border-b border-slate-100">
```

to:

```html
                <tr th:each="log, stat : ${logs.content}"
                    th:attr="data-href=@{/audit/action-logs/{id}(id=${log.actionLogId})}"
                    th:classappend="${stat.odd} ? 'bg-white' : 'bg-slate-50'"
                    class="border-b border-slate-100 cursor-pointer hover:bg-blue-50 transition-colors">
```

Then, right before the closing `</body>` tag, add the same click-handler script used in `record-audits.html`:

```html
<script>
    document.querySelectorAll('tbody tr[data-href]').forEach(function (row) {
        row.addEventListener('click', function () { window.location.href = row.dataset.href; });
    });
</script>
</body>
```

(replacing the current plain `</body>`)

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "com.company.aaamanagement.audit.AuditTemplateRenderingTest"`
Expected: PASS (all tests in the class).

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/templates/audit/action-logs.html src/test/java/com/company/aaamanagement/audit/AuditTemplateRenderingTest.java
git commit -m "Make Aksiyon Kayıtları list rows navigate to the new detail page"
```

---

### Task 5: Full verification

- [ ] **Step 1: Run the full test suite**

Run: `./gradlew test`
Expected: BUILD SUCCESSFUL, all tests green.

- [ ] **Step 2: Manual smoke check (optional but recommended)**

Run: `./gradlew bootRun`, then in a browser visit `/audit/action-logs`, click a row, confirm:
- The detail page loads at `/audit/action-logs/{id}`.
- `Kullanıcı Notu` shows the full note as plain text.
- `Ek Bilgi` shows a formatted table when `ExtraInfo` is valid JSON (with a working "Ham JSON göster/gizle" toggle), and shows plain text unchanged when it isn't JSON.
