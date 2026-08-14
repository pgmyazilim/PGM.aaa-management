package com.company.aaamanagement.common;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * DB'de UTC duvar saati olarak tutulan {@code *AtUtc} alanlarını sunum/giriş
 * katmanında yerel saat dilimine ({@link ZoneId#systemDefault()}) çevirir.
 * Yazılan değerler her zaman UTC kalır; dönüşüm sadece gösterim ve form
 * bağlama sırasında yapılır.
 */
@Service
public class LocalTimeService {

    public LocalDateTime toLocal(LocalDateTime utc) {
        if (utc == null) {
            return null;
        }
        return utc.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public LocalDateTime toUtc(LocalDateTime local) {
        if (local == null) {
            return null;
        }
        return local.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}
