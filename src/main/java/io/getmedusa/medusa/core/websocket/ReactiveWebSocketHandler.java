package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.injector.DOMChange;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;
import io.getmedusa.medusa.core.registry.PageTitleRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.registry.UIEventRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    public static final ObjectMapper MAPPER = setupObjectMapper();
    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();

    private static ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    private final UIEventRegistry registry = UIEventRegistry.getInstance();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        System.out.println("START of session : " + session.getId());
        return session.send(session.receive()
                        .map(msg -> interpretEvent(session, msg.getPayloadAsText()))
                        .map(session::textMessage).doFinally(sig -> {
                            System.out.println("END of session : " + session.getId());
                }));
    }

    private String interpretEvent(final WebSocketSession session, final String event) {
        String function = event;
        Object parameter = null;

        if(event.contains("(") && event.endsWith(")")) {
            String[] split = event.split("\\(");
            function = split[0];
            try {
                parameter = MAPPER.readValue(split[1].replace(")", ""), Object.class); //TODO multiple parameters
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        try {
            final List<DOMChange> domChanges = registry.execute(function, Arrays.asList(parameter));
            evaluateTitleChange(session, domChanges);
            evaluateConditionalChange(domChanges);
            return MAPPER.writeValueAsString(domChanges);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void evaluateConditionalChange(List<DOMChange> domChanges) {
        final Set<String> impactedDivIds = new HashSet<>();
        for(DOMChange domChange : domChanges) {
            if(domChange.getF() != null) {
               List<String> locallyImpactedIds = CONDITIONAL_REGISTRY.findByConditionField(domChange.getF());
               impactedDivIds.addAll(locallyImpactedIds);
            }
        }

        for(String impactedDivId : impactedDivIds) {
            DOMChange conditionCheck = new DOMChange(null, impactedDivId, DOMChange.DOMChangeType.CONDITION);
            conditionCheck.setC(CONDITIONAL_REGISTRY.get(impactedDivId));
            domChanges.add(conditionCheck);
        }
    }

    private void evaluateTitleChange(WebSocketSession session, List<DOMChange> domChanges) throws Exception {
        String unmappedTitle = PageTitleRegistry.getInstance().getTitle(session);
        if(unmappedTitle != null) {
            boolean hasAChange = false;
            for(DOMChange domChange : domChanges) {
                String searchKey = "[$" + domChange.getF() + "]";
                if(unmappedTitle.contains(searchKey)) {
                    hasAChange = true;
                    unmappedTitle = unmappedTitle.replaceAll("\\[\\$"+domChange.getF()+"\\]", domChange.getV().toString());
                }
            }
            if(hasAChange) {
                domChanges.add(new DOMChange(null, unmappedTitle, DOMChange.DOMChangeType.TITLE));
            }
        }
    }

}
