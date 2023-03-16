package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

class FluxUtilsTest {

    @Test
    void testDataBufferFluxToStringNull() {
        FluxUtils.dataBufferFluxToString(Flux.empty());
    }

    @Test
    void testDataBufferFluxToString() {
        DataBufferFactory factory = new DefaultDataBufferFactory();
        Flux<DataBuffer> flux = Flux.just(factory.wrap("hello world".getBytes(StandardCharsets.UTF_8)));
        String string = FluxUtils.dataBufferFluxToString(flux);
        Assertions.assertEquals("hello world", string);
    }

}
