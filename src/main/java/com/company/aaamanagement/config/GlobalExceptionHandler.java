package com.company.aaamanagement.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Kayıt Bulunamadı");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, Model model) {
        if (ex instanceof DuplicateKeyException || isDuplicateKey(ex)) {
            model.addAttribute("errorTitle", "Yinelenen Kayıt");
            model.addAttribute("errorMessage",
                    "Aynı benzersiz değere sahip bir kayıt zaten mevcut; kayıt eklenemedi.");
        } else {
            model.addAttribute("errorTitle", "Veri Bütünlüğü Hatası");
            model.addAttribute("errorMessage",
                    "Bu kayıt silinemez veya kaydedilemez: başka kayıtlarla ilişkisi olabilir.");
        }
        return "error";
    }

    // SQL Server unique index/constraint ihlalleri: hata kodu 2601 veya 2627
    private boolean isDuplicateKey(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql
                    && (sql.getErrorCode() == 2601 || sql.getErrorCode() == 2627)) {
                return true;
            }
        }
        return false;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArg(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorTitle", "Geçersiz İşlem");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex, Model model) {
        model.addAttribute("errorTitle", "İşlem Yapılamadı");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Beklenmeyen Hata");
        model.addAttribute("errorMessage", "Bir hata oluştu: " + ex.getMessage());
        return "error";
    }
}
