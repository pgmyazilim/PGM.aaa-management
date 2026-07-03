package com.company.aaamanagement.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES-GCM ile uygulama tarafı şifreleme (DatabaseCredentials.PasswordEncrypted,
 * OTP secret'ları). Anahtar koddan/DB'den değil, config/ortam değişkeninden gelir:
 * {@code aaa.crypto.key} = Base64 kodlu 16/24/32 baytlık anahtar.
 * Çıktı formatı: 12 baytlık rastgele IV + ciphertext + GCM tag.
 */
@Service
public class CryptoService {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public CryptoService(@Value("${aaa.crypto.key:}") String keyBase64) {
        if (keyBase64 == null || keyBase64.isBlank()) {
            this.key = null;
        } else {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64.trim());
            if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
                throw new IllegalStateException(
                        "aaa.crypto.key 16, 24 veya 32 baytlık Base64 kodlu bir AES anahtarı olmalı.");
            }
            this.key = new SecretKeySpec(keyBytes, "AES");
        }
    }

    public byte[] encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, requireKey(), new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] out = new byte[IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, out, 0, IV_LENGTH);
            System.arraycopy(ciphertext, 0, out, IV_LENGTH, ciphertext.length);
            return out;
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Şifreleme başarısız oldu.", e);
        }
    }

    public String decrypt(byte[] blob) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, requireKey(),
                    new GCMParameterSpec(TAG_LENGTH_BITS, blob, 0, IV_LENGTH));
            byte[] plaintext = cipher.doFinal(Arrays.copyOfRange(blob, IV_LENGTH, blob.length));
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Şifre çözme başarısız oldu.", e);
        }
    }

    private SecretKey requireKey() {
        if (key == null) {
            throw new IllegalStateException(
                    "Şifreleme anahtarı yapılandırılmamış: aaa.crypto.key (veya AAA_CRYPTO_KEY) ayarlayın.");
        }
        return key;
    }
}
