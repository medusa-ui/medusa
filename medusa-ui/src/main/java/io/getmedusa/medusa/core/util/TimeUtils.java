package io.getmedusa.medusa.core.util;

import java.time.Instant;

public class TimeUtils {

    private TimeUtils() {}

    public static long now() {
        return Instant.now().toEpochMilli();
    }

    public static long secondsDiff(long timeStart, long timeEnd) {
        return (long) Math.ceil((timeEnd - timeStart)/1000D);
    }

}
