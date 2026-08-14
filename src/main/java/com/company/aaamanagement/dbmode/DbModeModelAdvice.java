package com.company.aaamanagement.dbmode;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Aktif {@link DbMode}'u ("DEV"/"PROD") her sayfanın Model'ine ekler; sidebar
 * başlıkta ve toggle butonunda kullanılır.
 */
@ControllerAdvice
public class DbModeModelAdvice {

    @ModelAttribute("dbMode")
    public String dbMode(HttpSession session) {
        DbMode mode = session != null ? (DbMode) session.getAttribute(DbMode.SESSION_KEY) : null;
        return (mode != null ? mode : DbMode.DEV).name();
    }
}
