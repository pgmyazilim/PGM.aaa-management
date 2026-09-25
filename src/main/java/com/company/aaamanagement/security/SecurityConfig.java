package com.company.aaamanagement.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Tek kullanıcılı (env'den gelen) form girişi. Oturum sunucu tarafında HttpSession'da tutulur;
 * DB/Redis yoktur. /login dışındaki her istek kimlik doğrulaması ister, CSRF koruması açıktır.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true))
                .csrf(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(@Value("${aaa.auth.username}") String username,
                                                 @Value("${aaa.auth.password}") String password,
                                                 PasswordEncoder passwordEncoder) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "AAA_AUTH_USERNAME ve AAA_AUTH_PASSWORD tanımlanmalı (boş olamaz).");
        }
        byte[] expected = username.getBytes(StandardCharsets.UTF_8);
        String encodedPassword = passwordEncoder.encode(password);
        return requested -> {
            // Tam (büyük/küçük harf duyarlı) ve sabit zamanlı kullanıcı adı karşılaştırması
            if (requested == null
                    || !MessageDigest.isEqual(expected, requested.getBytes(StandardCharsets.UTF_8))) {
                throw new UsernameNotFoundException("Kullanıcı bulunamadı");
            }
            UserDetails user = User.withUsername(username)
                    .password(encodedPassword)
                    .authorities("ADMIN")
                    .build();
            return user;
        };
    }
}
