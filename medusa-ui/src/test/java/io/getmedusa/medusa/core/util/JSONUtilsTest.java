package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

class JSONUtilsTest {

    @Test
    void testSerializationLoop() {
        Map<String, Object> example = Map.of("X", 123, "Y", "ZYW");
        Map<String, Object> parsed = JSONUtils.deserialize(JSONUtils.serialize(example), Map.class);

        System.out.println(parsed);

    }

}
