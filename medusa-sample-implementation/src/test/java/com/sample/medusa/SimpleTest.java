package com.sample.medusa;

import com.sample.medusa.eventhandler.ExampleEventHandler;
import io.getmedusa.medusa.core.annotation.UIEventController;
import org.junit.jupiter.api.Test;

class SimpleTest {

    @Test
    void test() {
        System.out.println(new ExampleEventHandler() instanceof UIEventController);
    }

}
