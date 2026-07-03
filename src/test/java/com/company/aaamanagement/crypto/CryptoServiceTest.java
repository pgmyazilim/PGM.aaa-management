package com.company.aaamanagement.crypto;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CryptoServiceTest {

    private static final String KEY = Base64.getEncoder().encodeToString(new byte[32]);

    @Test
    void encryptDecrypt_roundTripsPlaintext() {
        CryptoService crypto = new CryptoService(KEY);

        byte[] blob = crypto.encrypt("çok-gizli-Parola!123");

        assertThat(crypto.decrypt(blob)).isEqualTo("çok-gizli-Parola!123");
    }

    @Test
    void encrypt_usesRandomIvSoCiphertextsDiffer() {
        CryptoService crypto = new CryptoService(KEY);

        assertThat(crypto.encrypt("aynı")).isNotEqualTo(crypto.encrypt("aynı"));
    }

    @Test
    void encrypt_withoutConfiguredKey_throwsIllegalState() {
        CryptoService crypto = new CryptoService("");

        assertThatThrownBy(() -> crypto.encrypt("x"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("aaa.crypto.key");
    }

    @Test
    void constructor_withWrongKeyLength_throwsIllegalState() {
        String shortKey = Base64.getEncoder().encodeToString(new byte[10]);

        assertThatThrownBy(() -> new CryptoService(shortKey))
                .isInstanceOf(IllegalStateException.class);
    }
}
