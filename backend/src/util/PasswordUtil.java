package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Simple password hashing using SHA-256 + salt.
 * For production, prefer BCrypt (org.mindrot.jbcrypt).
 * Demo passwords will be hashed with this utility.
 */
public class PasswordUtil {

    private static final String SALT_PREFIX = "SmartPorter$";

    public static String hashPassword(String plainPassword) {
        try {
            String salted = SALT_PREFIX + plainPassword;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(salted.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedHash) {
        String computed = hashPassword(plainPassword);
        return computed.equals(storedHash);
    }

    // Generate a random salt if needed for future enhancement
    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}