package com.company.aaamanagement.dbmode;

/**
 * Yönetim işlemlerinin uygulanacağı hedef veritabanı (AAA_DEV / AAA_PROD).
 * Aktif değer tarayıcı oturumunda ({@link #SESSION_KEY}) tutulur.
 */
public enum DbMode {
    DEV,
    PROD;

    public static final String SESSION_KEY = "dbMode";

    public DbMode toggle() {
        return this == DEV ? PROD : DEV;
    }
}
