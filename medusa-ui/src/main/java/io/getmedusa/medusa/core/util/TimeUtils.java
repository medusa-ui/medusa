package io.getmedusa.medusa.core.util;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private TimeUtils() {}

    public static long now() {
        return Instant.now().toEpochMilli();
    }

    public static long secondsDiff(long timeStart, long timeEnd) {
        try {
            return (long) Math.ceil((timeEnd - timeStart) / 1000D);
        } catch (Exception e) {
            return -1L;
        }
    }

    public static String diffString(long timeStart, long timeEnd) {
        long secDiff = secondsDiff(timeStart, timeEnd);

        long hours = TimeUnit.SECONDS.toHours(secDiff);
        long minutes = TimeUnit.SECONDS.toMinutes(secDiff) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.SECONDS.toSeconds(secDiff)- TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);

        StringBuilder diff = new StringBuilder();
        if(hours > 0) {
            diff.append(hours);
            diff.append("h ");
        }
        if(minutes > 0) {
            diff.append(minutes);
            diff.append("m ");
        }

        diff.append(seconds);
        diff.append("s");
        return diff.toString();
    }
}
