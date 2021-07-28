package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChanges.DOMChange;
import io.getmedusa.medusa.core.registry.ConditionalClassRegistry;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;
import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.registry.GenericMRegistry;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.registry.PageTitleRegistry;
import io.getmedusa.medusa.core.util.ExpressionEval;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles the lifecycle of the websocket session
 * Mostly contains logic on what to do when an event arrives serverside
 */
@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    public static final ObjectMapper MAPPER = setupObjectMapper();
    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();
    private static final ConditionalClassRegistry CONDITIONAL_CLASS_REGISTRY = ConditionalClassRegistry.getInstance();

    /**
     * JSON mapper setup
     * @return ObjectMapper
     */
    private static ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /**
     * Mono impl of the entire websocket session lifecycle. We send a response event (all changes due to event) to any event we receive.
     * Spring Reactive knows to kill the session as appropriate, which in turn will trigger the doFinally()
     * @param session active websocket session
     * @return full mono impl
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        System.out.println("START of session : " + session.getId());
        return session.send(session.receive()
                        .map(msg -> interpretEvent(session, msg.getPayloadAsText()))
                        .map(session::textMessage).doFinally(sig -> System.out.println("END of session : " + session.getId())));
    }

    /**
     * Chain of actions to trigger on an incoming event
     * @param session active websocket session
     * @param event incoming event as an unparsed string()
     * @return JSON version of all changes due to the event, to be sent as a websocket response
     */
    private String interpretEvent(final WebSocketSession session, final String event) {
        try {
            List<DOMChange> domChanges = executeEvent(session, event);
            evaluateTitleChange(session, domChanges);
            evaluateConditionalChange(domChanges);
            evaluateConditionalClassChange(domChanges);
            evaluateIterationChange(domChanges);
            evaluateGenericMAttributesChanged(domChanges);

            return MAPPER.writeValueAsString(domChanges);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute incoming event as a SpelExpression on the current UIEventController the WebSocketSession is attached to
     * @param session active websocket session
     * @param event incoming event as an unparsed string()
     * @return list of changes
     */
    private List<DOMChange> executeEvent(WebSocketSession session, String event) {
        try {
            List<DOMChange> domChanges = new ArrayList<>();
            final UIEventController eventController = EventHandlerRegistry.getInstance().get(session);
            final List<DOMChange> parsedExpressionValues = ExpressionEval.evalEventController(event, eventController).build();
            if (parsedExpressionValues != null) domChanges = new ArrayList<>(parsedExpressionValues);
            return domChanges;
        } catch (SpelEvaluationException e) {
            throw new IllegalArgumentException("Event '" + event + "' could not be executed", e);
        }
    }

    /**
     * Evaluate if any of the value changes would impact a condition. If so, send a CONDITION_CHECK back so that the UI can retry it
     * @param domChanges, potentially with added CONDITION_CHECK changes
     */
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

    /**
     * Evaluate if any of the value changes would impact a condition. If so, send a CONDITION_CHECK back so that the UI can retry it
     * @param domChanges, potentially with added CONDITION_CHECK changes
     */
    private void evaluateConditionalClassChange(List<DOMChange> domChanges) {
        final Set<String> impactedDivIds = new HashSet<>();
        for(DOMChange domChange : domChanges) {
            if(domChange.getF() != null) {
                List<String> locallyImpactedIds = CONDITIONAL_CLASS_REGISTRY.findByConditionField(domChange.getF());
                impactedDivIds.addAll(locallyImpactedIds);
            }
        }

        for(String impactedDivId : impactedDivIds) {
            DOMChange conditionCheck = new DOMChange(null, impactedDivId, DOMChange.DOMChangeType.CONDITIONAL_CLASS);
            conditionCheck.setC(CONDITIONAL_CLASS_REGISTRY.get(impactedDivId));
            domChanges.add(conditionCheck);
        }
    }

    /**
     * Evaluate if any of the value changes would impact a generic m-attribute. If so, send an M-ATTR back so that the UI can retry it
     * @param domChanges, potentially with added M-ATTR changes
     */
    private void evaluateGenericMAttributesChanged(List<DOMChange> domChanges) {
        final Set<String> impactedDivIds = new HashSet<>();
        for(DOMChange domChange : domChanges) {
            if(domChange.getF() != null) {
                List<String> locallyImpactedIds = GenericMRegistry.getInstance().findByConditionField(domChange.getF());
                impactedDivIds.addAll(locallyImpactedIds);
            }
        }

        for(String impactedDivId : impactedDivIds) {
            GenericMRegistry.RegistryItem registryItem = GenericMRegistry.getInstance().get(impactedDivId);
            DOMChange conditionCheck = new DOMChange(registryItem.attribute.name(), impactedDivId, DOMChange.DOMChangeType.M_ATTR);
            conditionCheck.setC(registryItem.expression);
            domChanges.add(conditionCheck);
        }
    }

    /**
     * Evaluate if any of the value changes would impact an iteration. If so, send a ITERATION back so that the UI can retry it
     * @param domChanges, potentially with added ITERATION changes
     */
    private void evaluateIterationChange(List<DOMChange> domChanges) {
        Map<String, String> templatesToUpdate = new HashMap<>();
        for(DOMChange domChange : domChanges) {
            Set<String> relatedTemplates = IterationRegistry.getInstance().findRelatedToValue(domChange.getF());
            if(relatedTemplates != null) {
                for (String relatedTemplate : relatedTemplates) {
                    templatesToUpdate.put(relatedTemplate, domChange.getF());
                }
            }
        }

        domChanges.addAll(templatesToUpdate.entrySet().stream()
                .map(entry -> new DOMChange(entry.getKey(), entry.getValue(), DOMChange.DOMChangeType.ITERATION))
                .collect(Collectors.toList()));
    }

    /**
     * Evaluate if any of the changes would affect the title. If so, return a TITLE event so the UI can reparse it
     * @param session active websocket session
     * @param domChanges domChanges so far, can additional be appended with TITLE events
     */
    private void evaluateTitleChange(WebSocketSession session, List<DOMChange> domChanges) {
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
