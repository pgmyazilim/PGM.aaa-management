package com.company.aaamanagement.permission;

import com.company.aaamanagement.domain.Action;
import com.company.aaamanagement.domain.GroupActionPermission;
import com.company.aaamanagement.domain.Module;
import com.company.aaamanagement.domain.UserGroup;
import org.junit.jupiter.api.Test;
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
 * Bir gruba atanmış bir yetkiyi (GroupActionPermission satırını) tamamen kaldırmak için
 * matrix.html'de PermissionController#delete'e (POST /permissions/{id}/delete) bağlı bir
 * "Sil" formu bulunmalı — sadece "İzin Var" checkbox'ı (allow/disallow toggle) yeterli değil.
 */
class PermissionMatrixTemplateRenderingTest {

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
    void permissionMatrixTemplate_rendersDeleteFormForEachAssignedPermission() {
        Module module = Module.builder().moduleId(9).name("Fatura").build();
        Action action = Action.builder().actionId(1).name("Fatura Sil").module(module).build();
        UserGroup group = UserGroup.builder().userGroupId(2).name("Yöneticiler").build();
        GroupActionPermission perm = GroupActionPermission.builder()
                .groupActionPermissionId(42).action(action).userGroup(group).allowed(true).build();

        Map<String, Object> vars = Map.of(
                "permissions", List.of(perm),
                "availableActions", List.of(),
                "groups", List.of(group),
                "modules", List.of(module),
                "selectedGroupId", 2,
                "activePage", "permissions"
        );

        String out = buildEngine().process("permission/matrix", realWebContext(vars));

        assertThat(out).contains("action=\"/permissions/42/delete\"");
        assertThat(out).contains("name=\"groupId\" value=\"2\"");
    }
}
