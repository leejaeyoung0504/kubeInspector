package com.eds.k8s.config;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    public static String encrypt(String value, String secret) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encrypted, String secret) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(original, "UTF-8");
    }
}