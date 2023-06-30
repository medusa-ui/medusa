package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.attributes.Attribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

class UIEventPageCallWrapperTest {

    @Test
    void testToMono() {
        List<Attribute> list = List.of(new Attribute("x", "y"));
        Assertions.assertEquals(list, UIEventPageCallWrapper.returnMonoVersion(list).block());
        Assertions.assertEquals(list, UIEventPageCallWrapper.returnMonoVersion(Mono.just(list)).block());
    }

}
