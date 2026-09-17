package com.company.aaamanagement.audit;

import com.company.aaamanagement.common.LocalTimeService;
import com.company.aaamanagement.domain.Action;
import com.company.aaamanagement.domain.ActionLog;
import com.company.aaamanagement.domain.Module;
import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.RecordAudit;
import com.company.aaamanagement.domain.Session;
import com.company.aaamanagement.domain.TrackedTable;
import com.company.aaamanagement.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.dialect.SpringStandardDialect;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Standalone Thymeleaf parse+process check for the audit templates, using a real
 * IWebContext (not the plain Context) so link expressions in layout/sidebar.html
 * resolve exactly as they would inside the running Spring MVC app.
 */
class AuditTemplateRenderingTest {

    private TemplateEngine buildEngine() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("src/main/resources/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        engine.setDialect(new SpringStandardDialect());
        return engine;
    }

    private IContext realWebContext(Map<String, Object> vars) {
        MockServletContext servletContext = new MockServletContext();
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setContextPath("");
        MockHttpServletResponse response = new MockHttpServletResponse();
        JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(servletContext);
        IServletWebExchange exchange = webApplication.buildExchange(request, response);
        WebContext ctx = new WebContext(exchange, Locale.forLanguageTag("tr"));

        // @localTimeService.toLocal(...) gibi Spring bean referanslarının şablonlarda
        // (örn. audit/record-audit-detail.html) çözülebilmesi için bean resolver'ı bağla.
        GenericApplicationContext beanContext = new GenericApplicationContext();
        beanContext.registerBean("localTimeService", LocalTimeService.class);
        beanContext.refresh();
        ctx.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(beanContext, null));

        vars.forEach(ctx::setVariable);
        return ctx;
    }

    private User user(int id, String first, String last) {
        return User.builder().userId(id).firstName(first).lastName(last).build();
    }

    @Test
    void recordAuditsListTemplate_parsesAndRendersWithRows() {
        User actor = user(1, "Ada", "Lovelace");
        TrackedTable table = TrackedTable.builder().trackedTableId(1).name("aaa.Users").build();
        RecordAudit row = RecordAudit.builder()
                .recordAuditId(42L)
                .trackedTable(table)
                .recordId("7")
                .operationType(OperationType.U)
                .actorUser(actor)
                .occurredAtUtc(LocalDateTime.now())
                .build();

        Map<String, Object> vars = Map.of(
                "audits", new PageImpl<>(List.of(row), PageRequest.of(0, 50), 1),
                "trackedTables", List.of(table),
                "operationTypes", OperationType.values(),
                "allUsers", List.of(actor),
                "activePage", "record-audits"
        );

        String out = buildEngine().process("audit/record-audits", realWebContext(vars));

        assertThat(out).contains("data-href=\"/audit/record-audits/42\"");
        assertThat(out).contains("aaa.Users");
        assertThat(out).doesNotContain("onclick");
    }

    @Test
    void recordAuditDetailTemplate_parsesAndRendersWithAndWithoutActionLog() {
        User actor = user(2, "Grace", "Hopper");
        TrackedTable table = TrackedTable.builder().trackedTableId(1).name("aaa.Users").build();
        Session session = Session.builder().sessionId(9L).sessionKey(UUID.randomUUID()).build();
        Module module = Module.builder().moduleId(1).name("Kimlik").build();
        Action action = Action.builder().actionId(1).module(module).name("Kullanıcı Güncelle").build();
        ActionLog actionLog = ActionLog.builder()
                .actionLogId(100L)
                .action(action)
                .success(true)
                .occurredAtUtc(LocalDateTime.now())
                .userNote("test notu")
                .extraInfo("test ek bilgi")
                .build();

        RecordAudit withLog = RecordAudit.builder()
                .recordAuditId(1L)
                .trackedTable(table)
                .recordId("5")
                .operationType(OperationType.I)
                .actorUser(actor)
                .session(session)
                .occurredAtUtc(LocalDateTime.now())
                .recordValues("{\"foo\":\"bar\"}")
                .extraInfo("kayıt ek bilgisi")
                .actionLog(actionLog)
                .build();

        String outWithLog = buildEngine().process("audit/record-audit-detail",
                realWebContext(Map.of("audit", withLog)));
        assertThat(outWithLog).contains("test notu");
        assertThat(outWithLog).contains("test ek bilgi");
        assertThat(outWithLog).contains("kayıt ek bilgisi");
        // Kayıt Değerleri artık istemci tarafında okunabilir tabloya dönüştürülüyor;
        // ham JSON gizli <pre> içinde korunuyor, biçimlendirme hedefi ve tetikleyici mevcut.
        assertThat(outWithLog).contains("id=\"recordValuesFormatted\"");
        assertThat(outWithLog).contains("id=\"recordValuesRaw\"");
        assertThat(outWithLog).contains("id=\"toggleRawRecordValues\"");
        assertThat(outWithLog).contains("{&quot;foo&quot;:&quot;bar&quot;}");

        RecordAudit withoutLog = RecordAudit.builder()
                .recordAuditId(2L)
                .trackedTable(table)
                .recordId("6")
                .operationType(OperationType.D)
                .actorUser(actor)
                .occurredAtUtc(LocalDateTime.now())
                .build();

        String outWithoutLog = buildEngine().process("audit/record-audit-detail",
                realWebContext(Map.of("audit", withoutLog)));
        assertThat(outWithoutLog).contains("Bağlı aksiyon logu yok");
    }

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
}
