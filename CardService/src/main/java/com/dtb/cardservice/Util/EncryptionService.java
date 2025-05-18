package com.dtb.cardservice.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionService {

    @Value("${security.encryption.key}")
    private String key;

    @Value("${security.encryption.iv}")
    private String initVector;

    private final String algo = "AES/CBC/PKCS5PADDING";


    /**
     * Encrypts a given string using AES encryption.
     *
     * @param value The string value to be encrypted.
     * @return The encrypted string, encoded in Base64.
     */
    public String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            log.error("Error encrypting ",ex);
        }
        return null;
    }

    /**
     * Decrypts a given string that was encrypted using AES encryption.
     *
     * @param encryptedString The encrypted string (Base64 encoded) to be decrypted.
     * @return The decrypted string.
     */
    public String decrypt(String encryptedString) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
            return new String(original);
        } catch (Exception ex) {
            log.error("Error decrypting ",ex);
        }
        return null;
    }
}
