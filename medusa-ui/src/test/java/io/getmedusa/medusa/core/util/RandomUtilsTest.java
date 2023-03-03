package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RandomUtilsTest {

    @Test
    void testGenerateId() {
        Assertions.assertNotNull(RandomUtils.generateId());
        Assertions.assertFalse(RandomUtils.generateId().isBlank());
        System.out.println(RandomUtils.generateId());
        for (int i = 0; i < 500; i++) {
            Assertions.assertFalse(RandomUtils.generateId().contains("/"));
        }
    }

    @Test
    void testHashGeneration() {
        String id1 = RandomUtils.generateId();
        String id2 = RandomUtils.generateId();

        String hashA = RandomUtils.generatePassword(id1);
        String hashB = RandomUtils.generatePassword(id2);
        String hashC = RandomUtils.generatePassword(id1);

        Assertions.assertNotNull(hashA);
        Assertions.assertNotNull(hashB);
        Assertions.assertNotNull(hashC);

        Assertions.assertEquals(hashA, hashC);
        Assertions.assertNotEquals(hashA, hashB);
        Assertions.assertNotEquals(hashC, hashB);

        Assertions.assertFalse(hashA.contains("/"));
        Assertions.assertFalse(hashB.contains("/"));
        Assertions.assertFalse(hashC.contains("/"));

        System.out.println(hashA);
    }

}
