package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimeUtilsTest {

    @Test
    void testNow() {
        Assertions.assertNotEquals(0L, TimeUtils.now());
    }

    @Test
    void testDiff() {
        Assertions.assertEquals(0, TimeUtils.secondsDiff(1656196065085L, 1656196065085L));
        Assertions.assertEquals(1, TimeUtils.secondsDiff(1656196065085L, 1656196065086L));
        Assertions.assertEquals(5, TimeUtils.secondsDiff(1656196065085L, 1656196069086L));
    }

}
