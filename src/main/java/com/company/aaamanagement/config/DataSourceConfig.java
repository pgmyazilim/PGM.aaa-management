package com.company.aaamanagement.config;

import com.company.aaamanagement.dbmode.DbMode;
import com.company.aaamanagement.dbmode.RoutingDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * AAA_DEV ve AAA_PROD için ayrı DataSource'lar; hangisinin kullanılacağı
 * request bazında {@link com.company.aaamanagement.dbmode.DataSourceContextHolder}
 * üzerinden {@link RoutingDataSource} tarafından seçilir. Bu bean'in varlığı
 * Spring Boot'un otomatik DataSourceAutoConfiguration'ının devreye girmesini
 * engeller (spring.datasource.url artık kullanılmıyor).
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${aaa.datasource.dev-url}")
    private String devUrl;

    @Value("${aaa.datasource.prod-url}")
    private String prodUrl;

    @Bean
    public DataSource devDataSource() {
        return buildDataSource(devUrl);
    }

    @Bean
    public DataSource prodDataSource() {
        return buildDataSource(prodUrl);
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSource devDataSource, DataSource prodDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DbMode.DEV, devDataSource);
        targetDataSources.put(DbMode.PROD, prodDataSource);

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(devDataSource);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    private DataSource buildDataSource(String url) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
}
