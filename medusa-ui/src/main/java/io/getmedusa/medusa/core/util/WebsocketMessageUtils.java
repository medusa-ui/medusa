package io.getmedusa.medusa.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;

import java.nio.charset.StandardCharsets;

public class WebsocketMessageUtils {

    private static final DataBufferFactory DATA_BUFFER_FACTORY = new DefaultDataBufferFactory();
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperBuilder.setupObjectMapper();

    private WebsocketMessageUtils() {}

    public static WebSocketMessage fromString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = DATA_BUFFER_FACTORY.wrap(bytes);
        return new WebSocketMessage(WebSocketMessage.Type.TEXT, buffer);
    }

    public static WebSocketMessage fromObject(Object obj) {
        try {
            return fromString(OBJECT_MAPPER.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}