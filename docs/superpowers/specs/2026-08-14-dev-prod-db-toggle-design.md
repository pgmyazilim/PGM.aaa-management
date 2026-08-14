# Tasarım: Dev/Prod Veritabanı Geçiş Butonu

**Tarih:** 2026-08-14
**Durum:** Onaylandı

## Problem

aaa-management tek bir sabit veritabanına (`AAA_DEV`) bağlı. Admin'lerin aynı
araçla bazen `AAA_DEV` bazen `AAA_PROD` üzerinde çalışabilmesi isteniyor;
geçiş sol menüdeki bir buton ve tam sayfa reload ile yapılacak, varsayılan
`DEV`.

## Kararlar

- **Kapsam:** Her tarayıcı oturumu (`HttpSession`) bağımsız seçim yapar.
  Uygulamada login/authentication katmanı yok (sadece
  `spring-security-crypto` hash için var), bu yüzden "kullanıcı" pratikte
  "tarayıcı oturumu" demek. Yeni oturumda (farklı tarayıcı, çerezler
  temizlenince) varsayılan `DEV`'e döner.
- **Prod'a geçişte onay:** Butona basınca (sadece DEV → PROD yönünde)
  JS `confirm()` ile "Prod veritabanına geçiyorsunuz, emin misiniz?" onayı
  istenir. PROD → DEV yönünde onay istenmez.
- **Bağlantı bilgisi:** AAA_PROD aynı sunucuda (10.99.100.117:1433), aynı
  kullanıcı adı/şifre; sadece `databaseName=AAA_PROD`.
- **DB'ye yazılan hiçbir şey değişmez** — bu, hangi fiziksel veritabanına
  bağlanılacağını seçen bir routing mekanizması; iş mantığı/repository
  katmanı hangi DB'de olduğunu bilmez.

## Çözüm

### Routing DataSource

`AbstractRoutingDataSource` (Spring çekirdek sınıfı) kullanılır — açılışta
hem dev hem prod için ayrı HikariCP pool'u oluşturulur, aktif pool seçimi
`ThreadLocal` üzerinden request bazında yapılır.

**`config/DataSourceConfig`** (yeni):
- `devDataSource()`, `prodDataSource()` — `DataSourceBuilder` ile, ortak
  `spring.datasource.username/password/driver-class-name` + ayrı
  `aaa.datasource.dev-url` / `aaa.datasource.prod-url`.
- `routingDataSource()` — `@Primary @Bean DataSource`; target map
  `{DEV: devDataSource, PROD: prodDataSource}`, default target dev.
  Bu bean var olduğu için Boot'un otomatik `DataSourceAutoConfiguration`'ı
  devreye girmez (`@ConditionalOnMissingBean(DataSource.class)`).

**`application.yml`**: `spring.datasource.url` kaldırılır, yerine
`aaa.datasource.dev-url` ve `aaa.datasource.prod-url` eklenir (mevcut
`AAA_DEV` URL'i `dev-url`'e taşınır, prod aynısının `databaseName=AAA_PROD`
hali).

### Aktif mod: `dbmode` paketi (yeni)

- **`DbMode`** — enum `DEV, PROD` + `public static final String SESSION_KEY = "dbMode"`.
- **`DataSourceContextHolder`** — `ThreadLocal<DbMode>`, `set/get/clear`.
  `get()` boşsa `DbMode.DEV` döner (örn. uygulama açılışında Hibernate
  `ddl-auto: validate` gibi request dışı bağlamlarda).
- **`RoutingDataSource extends AbstractRoutingDataSource`** —
  `determineCurrentLookupKey()` → `DataSourceContextHolder.get()`.
- **`DbModeFilter`** (`OncePerRequestFilter`, `@Component`) — her request
  başında session'daki `DbMode`'u (yoksa `DEV`) `DataSourceContextHolder`'a
  yazar, `finally` bloğunda temizler.
- **`DbModeController`** — `POST /db-mode/toggle`: session'daki modu
  DEV↔PROD çevirir, `Referer` header'ı varsa oraya, yoksa `/`'e
  `redirect:` ile döner (tam sayfa reload).
- **`DbModeModelAdvice`** (`@ControllerAdvice`) — `@ModelAttribute("dbMode")`
  ile aktif modu (session'dan) her sayfanın Model'ine otomatik ekler; tek
  tek controller'lara dokunmaya gerek kalmaz.

### Şablon: `layout/sidebar.html`

- Başlık: `AAA Yönetim (DEV)` / `AAA Yönetim (PROD)` —
  `th:text="'AAA Yönetim (' + ${dbMode} + ')'"`.
- En altta (nav listesinin dışında, `mt-auto` gerekmeden — `<ul>` zaten
  `flex-1` olduğu için sonrası doğal olarak dibe oturuyor) bir `<form
  method="post" th:action="@{/db-mode/toggle}">` içinde tek buton:
  - `dbMode == 'DEV'` ise metin "Prod'a Geç", `onsubmit` içinde
    `confirm('Prod veritabanına geçiyorsunuz, emin misiniz?')` — false
    dönerse submit iptal.
  - `dbMode == 'PROD'` ise metin "Dev'e Geç", onaysız submit.

## Riskler / Varsayımlar

- `AAA_PROD` veritabanının aynı sunucuda zaten var olduğu ve aynı
  kullanıcı/şifre ile erişilebilir olduğu varsayılıyor (kullanıcı onayladı).
  HikariCP açılışta her iki pool'u da doğrulamaya çalışır; `AAA_PROD`
  erişilemezse **uygulama hiç açılmaz** (dev dahil) — bu, routing
  DataSource'un doğası gereği kaçınılmaz bir side effect.
- Bu ortamda gerçek DB'lere ağ erişimi olmadığından canlı `bootRun` testi
  yapılamıyor; doğrulama `./gradlew compileJava` ile sınırlı kalacak.
