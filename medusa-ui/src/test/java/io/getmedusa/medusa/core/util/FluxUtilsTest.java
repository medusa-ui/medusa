package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.attributes.Attribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$;
import static io.getmedusa.medusa.core.attributes.Attribute.$$;

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

    @Test
    void testFluxJoin() {
        Flux<List<Attribute>> listA = Flux.just($$("listA_valueA", 1, "listA_valueB", 2));
        Mono<List<Attribute>> listB = Mono.just($$("listB_valueA", 3, "listB_valueB", 4));

        List<Attribute> list = FluxUtils.join(listA, listB).block();
        Assertions.assertEquals(4, list.size());
        Assertions.assertTrue(list.contains($("listA_valueA", 1)));
        Assertions.assertTrue(list.contains($("listA_valueB", 2)));
        Assertions.assertTrue(list.contains($("listB_valueA", 3)));
        Assertions.assertTrue(list.contains($("listB_valueB", 4)));
    }

}
