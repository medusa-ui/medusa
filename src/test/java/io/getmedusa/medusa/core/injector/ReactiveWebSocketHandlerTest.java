package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.websocket.Event;
import io.getmedusa.medusa.core.websocket.ReactiveWebSocketHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReactiveWebSocketHandlerTest {

    public static final ReactiveWebSocketHandler HANDLER = new ReactiveWebSocketHandler(null, null);

    @Test
    void parseTest() {
        final Event m = new Event("test");
        String parsed = HANDLER.toJSON(m);
        System.out.println(parsed);
        final Event e = HANDLER.toChatMessage(parsed);
        Assertions.assertEquals(e.getContent(), m.getContent());
    }


}
