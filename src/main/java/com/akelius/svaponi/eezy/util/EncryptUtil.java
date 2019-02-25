package com.akelius.svaponi.eezy.util;

import lombok.EqualsAndHashCode;
import org.springframework.cloud.context.encrypt.EncryptorFactory;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(of = "encryptKey")
public class EncryptUtil {

    public static final String cipher_prefix = "{cipher}";

    private static final EncryptorFactory factory = new EncryptorFactory();
    private static final Map<String, EncryptUtil> cache = new HashMap<>();

    /**
     * @param encryptKey key for encryption
     * @return same EncryptUtil instance with same encryption key (internal cache)
     */
    public static EncryptUtil getInstance(String encryptKey) {
        Assert.hasText(encryptKey, "invalid or empty key");
        return cache.computeIfAbsent(encryptKey, key -> build(key));
    }

    /**
     * @param encryptKey key for encryption
     * @return a new EncryptUtil instance
     */
    public static EncryptUtil build(String encryptKey) {
        Assert.hasText(encryptKey, "invalid or empty key");
        return new EncryptUtil(encryptKey);
    }

    /*
        instance ...
     */

    private final String encryptKey;
    private final TextEncryptor textEncryptor;

    // hidden constructor, use static build() instead
    private EncryptUtil(String encryptKey) {
        Assert.hasText(encryptKey, "invalid or empty key");
        this.encryptKey = encryptKey;
        this.textEncryptor = factory.create(encryptKey);
    }

    /**
     * @param plainText text to be encrypted
     * @return encrypted text
     */
    public String encrypt(final String plainText) {
        Assert.isTrue(!plainText.startsWith(cipher_prefix), "already encrypted");
        return textEncryptor.encrypt(plainText);
    }

    /**
     * @param plainText text to be encrypted
     * @return encrypted text with proper prefix (SpringBoot complaint)
     */
    public String encryptWithPrefix(final String plainText) {
        return cipher_prefix + encrypt(plainText);
    }

    /**
     * @param encryptedText text to be decrypted
     * @return decrypted text
     */
    public String decrypt(final String encryptedText) {
        final String encrypted = encryptedText.startsWith(cipher_prefix) ? encryptedText.substring(cipher_prefix.length()) : encryptedText;
        return textEncryptor.decrypt(encrypted);
    }
}