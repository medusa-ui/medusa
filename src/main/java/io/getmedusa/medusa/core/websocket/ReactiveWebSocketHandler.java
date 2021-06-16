package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.injector.DOMChange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive()
                        .map(msg -> interpretEvent(msg.getPayloadAsText()))
                        .map(session::textMessage));
    }

    int counter = 0;

    private String interpretEvent(final String event) {
        System.out.println("Interpret: " + event);
        String function = event;
        String parameter = null;
        if(event.contains("(") && event.endsWith(")")) {
            String[] split = event.split("\\(");
            function = split[0];
            parameter = split[1].replace(")", "");
        }

        if("increaseCounter".equals(function)) {
            try {
                if(parameter == null) parameter = "0";
                counter += Integer.parseInt(parameter);
                return MAPPER.writeValueAsString(
                        Collections.singletonList(new DOMChange("counter-value", Integer.toString(counter)))
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String domChange = UUID.randomUUID().toString();
        return domChange;
    }

}
