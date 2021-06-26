package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChange;
import io.getmedusa.medusa.core.injector.HTMLInjector;
import io.getmedusa.medusa.core.registry.*;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    public static final ObjectMapper MAPPER = setupObjectMapper();
    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();
    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    private static ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        System.out.println("START of session : " + session.getId());
        return session.send(session.receive()
                        .map(msg -> interpretEvent(session, msg.getPayloadAsText()))
                        .map(session::textMessage).doFinally(sig -> System.out.println("END of session : " + session.getId())));
    }

    private String interpretEvent(final WebSocketSession session, final String event) {
        try {
            final List<DOMChange> domChanges = new ArrayList<>();
            final Expression parsedExpression = SPEL_EXPRESSION_PARSER.parseExpression(event);
            if(event.startsWith("changePage(")) {
                domChanges.addAll((List<DOMChange>) parsedExpression.getValue(this));
            } else {
                final UIEventController eventController = EventHandlerRegistry.getInstance().get(session);
                domChanges.addAll((List<DOMChange>) parsedExpression.getValue(eventController));
                evaluateTitleChange(session, domChanges);
                evaluateConditionalChange(domChanges);
            }
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

    public List<DOMChange> changePage(String endpoint) {
        List<DOMChange> domChanges = new ArrayList<>();

        PageSetup file = RouteRegistry.getInstance().getPageSetupFromPath(endpoint);
        final String fileName = file.getHtmlFile();
        final String htmlLoaded = HTMLInjector.INSTANCE.inject(file.getGetPath(), fileName, null);
        final String title = parseTitle(htmlLoaded);
        final DOMChange pageLoad = new DOMChange(title, htmlLoaded);
        pageLoad.setT(DOMChange.DOMChangeType.PAGE_CHANGE.ordinal());
        pageLoad.setC(file.getGetPath());
        domChanges.add(pageLoad);

        return domChanges;
    }

    private String parseTitle(String htmlLoaded) {
        String title = null;
        Matcher matcher = Pattern.compile("<title>.*?</title>").matcher(htmlLoaded);
        if (matcher.find()) {
            title = matcher.group(0).replace("<title>", "").replace("</title>", "");
        }
        return title;
    }

}
