# Aksiyon Kayıtları Detay Ekranı

## Amaç

Aksiyon Kayıtları (`ActionLog`) listesinde `UserNote` ve `ExtraInfo` alanları şu an görünmüyor
(`UserNote` listede 60 karaktere kısaltılmış önizleme olarak var, `ExtraInfo` hiç yok). Kayıt
Denetimi (`RecordAudit`) ekranındaki gibi bir detay sayfası ekleyerek bu alanların tam içeriğini
göstermek. `ExtraInfo` bazen JSON, bazen düz metin olabildiği için, Kayıt Denetimi'nin
`recordValues` alanında kullanılan "JSON parse dene, olmazsa düz metin göster" deseni burada da
uygulanacak.

## Kapsam

- Yeni: `GET /audit/action-logs/{id}` route + `audit/action-log-detail.html` template.
- Değişiklik: `action-logs.html` listesindeki satırlar, Kayıt Denetimi'nde olduğu gibi tıklanınca
  detay sayfasına yönlendirecek (`data-href` + JS).
- Kapsam dışı: DB şeması değişikliği yok (`ExtraInfo`/`UserNote` zaten `nvarchar(max)`), liste
  filtreleri değişmiyor, genel/paylaşılan bir modal bileşeni oluşturulmuyor (proje genelinde böyle
  bir bileşen yok; Kayıt Denetimi deseni de tam sayfa, modal değil).

## Backend

**`AuditService`** — `findRecordAuditById` ile birebir aynı desende yeni metod:

```java
public ActionLog findActionLogById(Long id) {
    return actionLogRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aksiyon logu bulunamadı: " + id));
}
```

**`AuditController`** — `recordAuditDetail` ile aynı desende yeni endpoint:

```java
@GetMapping("/action-logs/{id}")
public String actionLogDetail(@PathVariable("id") Long id, Model model) {
    model.addAttribute("log", auditService.findActionLogById(id));
    model.addAttribute("activePage", "action-logs");
    return "audit/action-log-detail";
}
```

## Frontend — Liste (`action-logs.html`)

- Satır `<tr>`'ye `record-audits.html` ile aynı desende `th:attr="data-href=@{/audit/action-logs/{id}(id=${log.actionLogId})}"`
  ve `cursor-pointer hover:bg-blue-50 transition-colors` sınıfları eklenir.
- Sayfa sonuna, `record-audits.html`'deki ile aynı satır-tıklama script'i eklenir.
- Mevcut filtreler, sayfalama, "Not" önizleme sütunu değişmeden kalır.

## Frontend — Detay (`action-log-detail.html`, yeni dosya)

`record-audit-detail.html` ile aynı iskelet (sidebar, geri linki, kart düzeni, Tailwind CDN).

**Genel Bilgiler kartı** — `ActionLog` alanlarına göre: İşlem (`log.action.name`), Kullanıcı
(`log.actorUser.fullName`, null ise `—`), Sonuç (başarılı/başarısız rozeti, mevcut listedeki
stille aynı), Tarih (`log.occurredAtUtc`, `localTimeService.toLocal` ile yerel saate çevrilip
`dd.MM.yyyy HH:mm:ss` formatında).

**Kullanıcı Notu kartı** — düz metin, `whitespace-pre-wrap`, null ise "— Not yok —"
(`record-audit-detail.html`'deki `actionLog.userNote` gösterimiyle birebir aynı; JSON denemesi
yapılmaz, çünkü bu alan serbest metin notu).

**Ek Bilgi (ExtraInfo) kartı** — `record-audit-detail.html`'deki `recordValues` JSON-parse-dene
desenimin birebir kopyası, `extraInfo` alanına uygulanır:
- Gizli `<pre id="extraInfoRaw">` içinde ham metin (`th:text="${log.extraInfo}"`).
- Boş `<div id="extraInfoFormatted">`.
- "Ham JSON göster/gizle" toggle butonu (başlangıçta gizli, JS tarafından JSON parse başarılı
  olursa gösterilir).
- `<script th:inline="none">` bloğu: `JSON.parse` dene → başarılıysa `render()`/`kvTable()`/
  `list()`/`changesTable()` ile biçimlendirilmiş tablo/liste üretir, ham `<pre>`'i gizler, toggle
  butonunu aktifleştirir; başarısızsa (catch) hiçbir şey yapmadan döner, ham `<pre>` düz metin
  olarak görünmeye devam eder.
- `extraInfo == null` ise "— Ek bilgi yok —" mesajı (kart içindeki `<div>` hiç render edilmez, JS
  çalışmaz).

## Test

- `aaa-management`'ta gerçek JUnit testleri var; değişiklik sonrası `./gradlew test` çalıştırılıp
  yeşil olduğu doğrulanacak.
- Yeni controller endpoint'i için (varsa mevcut `AuditController`/`AuditService` test dosyalarının
  deseni izlenerek) `findActionLogById` bulunamadığında `EntityNotFoundException` fırlattığını
  doğrulayan bir test eklenmesi değerlendirilecek (mevcut `findRecordAuditById` için eşdeğer test
  varsa onun deseni izlenir).
