package com.ssmnd.studentintellect.security;

import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import android.util.Base64;
import javax.crypto.*;

public class RSACipher {
    private final byte[] publicKeyBytes;
    private byte[] privateKeyBytes = null;

    public RSACipher() {
        KeyPair keyPair = generateKeyPair();
        publicKeyBytes = keyPair.getPublic().getEncoded();
        privateKeyBytes = keyPair.getPrivate().getEncoded();
    }
    public RSACipher(@NotNull KeyPair keyPair) {
        publicKeyBytes = keyPair.getPublic().getEncoded();
        privateKeyBytes = keyPair.getPrivate().getEncoded();
    }
    public RSACipher(@NotNull PublicKey publicKey, @NotNull PrivateKey privateKey) {
        publicKeyBytes = publicKey.getEncoded();
        privateKeyBytes = privateKey.getEncoded();
    }
    public RSACipher(@NotNull String publicKey, @NotNull String privateKey) {
        publicKeyBytes = Base64.decode(publicKey.getBytes(StandardCharsets.UTF_16LE), 0);
        privateKeyBytes = Base64.decode(privateKey.getBytes(StandardCharsets.UTF_16LE), 0);
    }
    public RSACipher(@NotNull String publicKey) {
        publicKeyBytes = Base64.decode(publicKey.getBytes(StandardCharsets.UTF_16LE), 0);
    }
    public RSACipher(byte[] publicKey, byte[] privateKey) {
        publicKeyBytes = publicKey;
        privateKeyBytes = privateKey;
    }
    public RSACipher(byte[] publicKey) {
        publicKeyBytes = publicKey;
    }

    @NotNull
    public static String getKeyString(@NotNull Key key) {
        byte[] bytes = Base64.encodeToString(key.getEncoded(), 0).getBytes();
        return new String(bytes, StandardCharsets.UTF_16LE);
    }
    @NotNull
    public static String getKeyString(byte[] keyBytes) {
        byte[] bytes = Base64.encodeToString(keyBytes, 0).getBytes();
        return new String(bytes, StandardCharsets.UTF_16LE);
    }

    @NotNull
    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            keyPair = keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyPair != null;
        return keyPair;
    }

    @NotNull
    public String getPublicKey() {
        return getKeyString(publicKeyBytes);
    }
    @NotNull
    public String getPrivateKey() {
        return getKeyString(privateKeyBytes);
    }

    public String encrypt(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        try {
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
            String encryptedString = Base64.encodeToString(encryptedBytes, 0);
            return new String(encryptedString.getBytes(), StandardCharsets.UTF_16LE);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String encrypt(String string, @NotNull PublicKey publicKey) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
            String encryptedString = Base64.encodeToString(encryptedBytes, 0);
            return new String(encryptedString.getBytes(), StandardCharsets.UTF_16LE);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String encrypt(String string, @NotNull String publicKeyString) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        try {
            byte[] publicKeyBytes = Base64.decode(publicKeyString.getBytes(StandardCharsets.UTF_16LE), 0);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

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
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);


            byte[] encryptedBytes = Base64.decode(string.getBytes(StandardCharsets.UTF_16LE), 0);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String decrypt(String string, @NotNull PrivateKey privateKey) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.decode(string.getBytes(StandardCharsets.UTF_16LE), 0);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String decrypt(String string, @NotNull String privateKeyString) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        try {
            byte[] privateKeyBytes = Base64.decode(privateKeyString.getBytes(StandardCharsets.UTF_16LE), 0);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.decode(string.getBytes(StandardCharsets.UTF_16LE), 0);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
