package com.company.aaamanagement.layout;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.dialect.SpringStandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The searchable-dropdown enhancement (layout/select-search.html) is pulled into every page
 * through layout/sidebar.html, so any page that renders selects must still parse/render
 * cleanly and keep its native <select name="..."> elements (Spring MVC form binding depends
 * on them staying in the DOM — the enhancer only visually overlays them).
 */
class SelectSearchTemplateRenderingTest {

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
        vars.forEach(ctx::setVariable);
        return ctx;
    }

    @Test
    void infrastructureModulesTemplate_rendersSelectAndEnhancementScriptOnce() {
        Map<String, Object> vars = Map.of(
                "modules", new PageImpl<>(List.of()),
                "projects", List.of(),
                "activePage", "infrastructure"
        );

        String out = buildEngine().process("infrastructure/modules", realWebContext(vars));

        assertThat(out).contains("<select name=\"projectId\"");
        assertThat(countOccurrences(out, "function enhanceSelect")).isEqualTo(1);
    }

    @Test
    void permissionMatrixTemplate_rendersFilterSelectsAndEnhancementScriptOnce() {
        Map<String, Object> vars = Map.of(
                "groups", List.of(),
                "modules", List.of(),
                "activePage", "permissions"
        );

        String out = buildEngine().process("permission/matrix", realWebContext(vars));

        assertThat(out).contains("<select name=\"groupId\"");
        assertThat(out).contains("<select name=\"moduleId\"");
        assertThat(countOccurrences(out, "function enhanceSelect")).isEqualTo(1);
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        int index = 0;
        while ((index = haystack.indexOf(needle, index)) != -1) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
