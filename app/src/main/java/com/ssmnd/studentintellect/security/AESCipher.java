package com.ssmnd.studentintellect.security;
import android.util.Base64;
import org.jetbrains.annotations.NotNull;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AESCipher {
    private final byte[] secretKeyBytes;

    public AESCipher() {
        secretKeyBytes = generateSecretKey().getBytes(StandardCharsets.UTF_16LE);
    }
    public AESCipher(byte[] secretKey) {
        this.secretKeyBytes = secretKey;
    }
    public AESCipher(@NotNull String secretKey) {
        this.secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_16LE);
    }

    @NotNull
    public static String generateSecretKey() {
        byte[] secretKey = new byte[16];
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); //32 bytes
            secretKey = keyGenerator.generateKey().getEncoded();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new String(secretKey, StandardCharsets.UTF_16LE);
    }
    @NotNull
    public String getSecretKey() {
        return new String(secretKeyBytes, StandardCharsets.UTF_16LE);
    }

    public String encrypt(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(secretKeyBytes, 16));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
            String encryptedString = Base64.encodeToString(encryptedBytes, 0);
            return new String(encryptedString.getBytes(), StandardCharsets.UTF_16LE);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String encrypt(String string, @NotNull String encryptionKey) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        byte[] secretKey = encryptionKey.getBytes(StandardCharsets.UTF_16LE);
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(secretKey, 16));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
            String encryptedString = Base64.encodeToString(encryptedBytes, 0);
            return new String(encryptedString.getBytes(), StandardCharsets.UTF_16LE);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(secretKeyBytes, 16));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = Base64.decode(string.getBytes(StandardCharsets.UTF_16LE), 0);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String decrypt(String string, @NotNull String decryptionKey) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        byte[] secretKey = decryptionKey.getBytes(StandardCharsets.UTF_16LE);
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(secretKey, 16));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = Base64.decode(string.getBytes(StandardCharsets.UTF_16LE), 0);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
