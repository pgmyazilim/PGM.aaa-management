package com.company.aaamanagement.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityConfigTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecurityConfig config = new SecurityConfig();

    @Test
    void userDetailsService_returnsConfiguredUserWithEncodedPassword() {
        UserDetailsService uds = config.userDetailsService("admin", "s3cret", encoder);

        UserDetails user = uds.loadUserByUsername("admin");

        assertThat(user.getPassword()).isNotEqualTo("s3cret");
        assertThat(encoder.matches("s3cret", user.getPassword())).isTrue();
        assertThat(encoder.matches("wrong", user.getPassword())).isFalse();
    }

    @Test
    void userDetailsService_usernameIsCaseSensitive() {
        UserDetailsService uds = config.userDetailsService("admin", "s3cret", encoder);

        assertThatThrownBy(() -> uds.loadUserByUsername("ADMIN"))
                .isInstanceOf(UsernameNotFoundException.class);
        assertThatThrownBy(() -> uds.loadUserByUsername("other"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void userDetailsService_blankCredentials_failFast() {
        assertThatThrownBy(() -> config.userDetailsService("", "x", encoder))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> config.userDetailsService("admin", " ", encoder))
                .isInstanceOf(IllegalStateException.class);
    }
}
