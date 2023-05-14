package io.getmedusa.medusa.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public final class JSONUtils {

    private static final ObjectMapper OBJECT_MAPPER = buildObjectMapper();

    private JSONUtils() {}

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper m = new ObjectMapper();
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        m.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        m.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        m.registerModule(new JavaTimeModule());

        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new JsonDeserializer<>() {
            @Override
            public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                long epochTime = Long.parseLong(p.getText());
                return Instant.ofEpochMilli(epochTime).atZone(ZoneId.of("UTC")).toLocalDate();
            }
        });
        m.registerModule(module);

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

    public static <T> List<T> deserializeList(String json, Class<T> clazz) {
        if(json.isBlank()) {
            return List.of();
        }
        try {
            return (List<T>) OBJECT_MAPPER.readValue(json, List.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
