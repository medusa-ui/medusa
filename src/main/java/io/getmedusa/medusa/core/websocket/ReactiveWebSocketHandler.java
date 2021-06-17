package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.injector.DOMChange;
import io.getmedusa.medusa.core.registry.PageTitleRegistry;
import io.getmedusa.medusa.core.registry.UIEventRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final UIEventRegistry registry = UIEventRegistry.getInstance();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive()
                        .map(msg -> interpretEvent(session, msg.getPayloadAsText()))
                        .map(session::textMessage));
    }

    private String interpretEvent(final WebSocketSession session, final String event) {
        String function = event;
        String parameter = null;

        if(event.contains("(") && event.endsWith(")")) {
            String[] split = event.split("\\(");
            function = split[0];
            parameter = split[1].replace(")", "");
        }

        try {
            List<DOMChange> domChanges = registry.execute(function, Arrays.asList(parameter));
            evaluateTitleChange(session, domChanges);
            return MAPPER.writeValueAsString(domChanges);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void evaluateTitleChange(WebSocketSession session, List<DOMChange> domChanges) throws Exception {
        String unmappedTitle = PageTitleRegistry.getInstance().getTitle(session);
        if(unmappedTitle != null) {
            for(DOMChange domChange : domChanges) {
                String searchKey = "[$" + domChange.getF() + "]";
                if(unmappedTitle.contains(searchKey)) {
                    unmappedTitle = unmappedTitle.replaceAll("\\[\\$"+domChange.getF()+"\\]", domChange.getV());
                }
            }
            domChanges.add(new DOMChange(null, unmappedTitle, DOMChange.DOMChangeType.TITLE));
        }
    }

}
