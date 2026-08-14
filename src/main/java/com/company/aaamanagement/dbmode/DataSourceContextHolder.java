package com.company.aaamanagement.dbmode;

/**
 * O anki request'in hangi veritabanına yönlendirileceğini tutan ThreadLocal.
 * {@link RoutingDataSource} her bağlantı isteğinde buradan okur.
 */
public final class DataSourceContextHolder {

    private static final ThreadLocal<DbMode> CURRENT_MODE = new ThreadLocal<>();

    private DataSourceContextHolder() {
    }

    public static void set(DbMode mode) {
        CURRENT_MODE.set(mode);
    }

    // Request dışı bağlamlarda (örn. açılışta Hibernate şema doğrulaması) DEV varsayılan.
    public static DbMode get() {
        DbMode mode = CURRENT_MODE.get();
        return mode != null ? mode : DbMode.DEV;
    }

    public static void clear() {
        CURRENT_MODE.remove();
    }
}
