package com.company.aaamanagement.dbmode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Her request'in başında tarayıcı oturumundaki aktif {@link DbMode}'u
 * {@link DataSourceContextHolder}'a yazar; request bitince temizler.
 */
@Component
public class DbModeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        DbMode mode = session != null ? (DbMode) session.getAttribute(DbMode.SESSION_KEY) : null;
        DataSourceContextHolder.set(mode != null ? mode : DbMode.DEV);
        try {
            filterChain.doFilter(request, response);
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}
