package io.getmedusa.medusa;

import io.getmedusa.medusa.core.annotation.UIEventController;
import org.junit.jupiter.api.Test;

class SimpleTest {

    @Test
    void test() {
        System.out.println(new ExampleEventHandler() instanceof UIEventController);
    }

}
