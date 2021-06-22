package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.injector.DOMChange;
import io.getmedusa.medusa.core.injector.HTMLInjector;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;
import io.getmedusa.medusa.core.registry.PageTitleRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.registry.UIEventRegistry;
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
                final String unparsedParameter = split[1].replace(")", "").trim();
                if(!unparsedParameter.isEmpty()) {
                    parameter = MAPPER.readValue(unparsedParameter, Object.class); //TODO multiple parameters
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e);
            }
        }

        try {
            final List<DOMChange> domChanges;
            if(event.startsWith("changePage(")) {
                domChanges = loadPage(parameter);
            } else {
                domChanges = registry.execute(function, Arrays.asList(parameter));
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

    private List<DOMChange> loadPage(Object parameter) {
        List<DOMChange> domChanges = new ArrayList<>();

        PageSetup file = RouteRegistry.getInstance().getPageSetupFromPath(parameter.toString());
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
            title = matcher.group(0);
        }
        return title;
    }

}
