package io.getmedusa.medusa.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class JSONUtils {

    private static final ObjectMapper OBJECT_MAPPER = buildObjectMapper();

    private JSONUtils() {}

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper m = new ObjectMapper();
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule simpleModule = new SimpleModule();
        //simpleModule.addKeyDeserializer(Item.class, new ItemDeserializer());
        m.registerModule(simpleModule);

        return m;
    }

    public static String serialize(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T deserialize(String key, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(key, clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
