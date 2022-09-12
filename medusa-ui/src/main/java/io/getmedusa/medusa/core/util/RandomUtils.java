package io.getmedusa.medusa.core.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

public final class RandomUtils {

    private RandomUtils() {}

    public static String generateId() {
        final String newId = System.nanoTime() +
                UUID.randomUUID().toString() +
                new SecureRandom().nextInt(99999);
        return newId.replace("-", "");
    }

    public static String generatePassword(String id) {
        try {
            return createSHAHash(id);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createSHAHash(final String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest =  md.digest(input.getBytes(StandardCharsets.UTF_8));
        return convertToHex(messageDigest);
    }

    private static String convertToHex(final byte[] messageDigest) {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}
