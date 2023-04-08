package io.getmedusa.medusa.core.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public final class RandomUtils {

    private static final int PASSWORD_LENGTH = 45;

    private RandomUtils() {}

    public static String generateId() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[PASSWORD_LENGTH];
        random.nextBytes(bytes);
        return createHash(bytes);
    }

    public static String generatePassword(String id) {
        return createHash(id.getBytes(StandardCharsets.UTF_8));
    }

    private static String createHash(final byte[] input) {
        return Base64.getEncoder().encodeToString(input).replace("/", "+");
    }
}
