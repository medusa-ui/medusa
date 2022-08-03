package io.getmedusa.medusa.core.util;

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
}
