package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.common.LocalTimeService;
import com.company.aaamanagement.domain.Client;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTemplateRenderingTest {

    private static final UUID KEY = UUID.fromString("3f2504e0-4f89-11d3-9a0c-0305e82c3301");

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

        GenericApplicationContext beanContext = new GenericApplicationContext();
        beanContext.registerBean("localTimeService", LocalTimeService.class);
        beanContext.refresh();
        ctx.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(beanContext, null));

        vars.forEach(ctx::setVariable);
        return ctx;
    }

    @Test
    void clientsList_showsKeyAndHasNoDeleteAction() {
        Client c = Client.builder().clientId(3).clientKey(KEY).name("Gocsis Arayüzü").build();

        String out = buildEngine().process("infrastructure/clients", realWebContext(Map.of(
                "clients", new PageImpl<>(List.of(c), PageRequest.of(0, 20), 1),
                "activePage", "infrastructure")));

        assertThat(out).contains("Gocsis Arayüzü");
        assertThat(out).contains("3f2504e0-4f89-11d3-9a0c-0305e82c3301");
        assertThat(out).contains("/infrastructure/clients/3/edit");
        assertThat(out).doesNotContain("/delete");
    }

    @Test
    void clientForm_new_hasNoKeyField_edit_showsKeyReadOnly() {
        String outNew = buildEngine().process("infrastructure/client-form", realWebContext(Map.of(
                "client", new Client(), "activePage", "infrastructure")));
        assertThat(outNew).contains("Yeni İstemci");
        assertThat(outNew).doesNotContain("name=\"clientKey\"");

        Client existing = Client.builder().clientId(3).clientKey(KEY).name("Gocsis Arayüzü").build();
        String outEdit = buildEngine().process("infrastructure/client-form", realWebContext(Map.of(
                "client", existing, "activePage", "infrastructure")));
        assertThat(outEdit).contains("İstemci Düzenle");
        assertThat(outEdit).contains("3f2504e0-4f89-11d3-9a0c-0305e82c3301");
        assertThat(outEdit).contains("readonly");
        assertThat(outEdit).doesNotContain("name=\"clientKey\"");
    }
}
