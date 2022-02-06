package io.getmedusa.medusa.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperBuilder {
    /**
     * JSON mapper setup
     * @return ObjectMapper
     */
    public static ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.USE_DEFAULTS);
        return objectMapper;
    }

}
