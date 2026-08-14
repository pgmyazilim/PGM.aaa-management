package com.company.aaamanagement.dbmode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/db-mode")
public class DbModeController {

    @PostMapping("/toggle")
    public String toggle(HttpServletRequest request, @RequestHeader(value = "Referer", required = false) String referer) {
        HttpSession session = request.getSession(true);
        DbMode current = (DbMode) session.getAttribute(DbMode.SESSION_KEY);
        DbMode next = (current != null ? current : DbMode.DEV).toggle();
        session.setAttribute(DbMode.SESSION_KEY, next);
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/");
    }
}
