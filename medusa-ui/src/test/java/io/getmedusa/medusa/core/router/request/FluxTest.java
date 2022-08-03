package io.getmedusa.medusa.core.router.request;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxTest {

    @Test
    void testMe() {
        final Flux<Integer> flux = Flux.just(1, 2, 3, 4);

    }

}
