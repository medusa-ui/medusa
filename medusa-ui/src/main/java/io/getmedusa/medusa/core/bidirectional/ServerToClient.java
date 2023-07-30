package io.getmedusa.medusa.core.bidirectional;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.getmedusa.diffengine.Engine;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.router.action.DataChunk;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.getmedusa.medusa.core.util.AttributeUtils.mergeDiffs;

/**
 * Exposed the ability to easily send {@link Attribute} from server to client.
 */
@Component
public class ServerToClient {

    private final SessionMemoryRepository sessionMemoryRepository;
    private final Renderer renderer;
    private final Engine diffEngine;

    public ServerToClient(SessionMemoryRepository sessionMemoryRepository, Renderer renderer) {
        this.sessionMemoryRepository = sessionMemoryRepository;
        this.renderer = renderer;
        this.diffEngine = new Engine();
    }

    /**

     * Send attribute updates from the server to the client via session tag.
     *
     * @param attributes Collection of {@link Attribute} to send
     * @param sessionTagKey See {@link io.getmedusa.medusa.core.session.StandardSessionTagKeys} or use a custom tag (Required)
     * @param sessionTagValue See {@link io.getmedusa.medusa.core.session.StandardSessionTagValues} or use a custom tag (Required)
     */
    public void sendAttributesToSessionTag(Collection<Attribute> attributes, String sessionTagKey, String sessionTagValue) {
        if(sessionTagKey == null) {
            throw new IllegalArgumentException("Session tag must be provided");
        }
        if(attributes == null || attributes.isEmpty()) {
            return;
        }

        sendAttributesToSession(attributes, sessionMemoryRepository.findSessionsByTag(sessionTagKey, sessionTagValue));
    }

    /**
     * Send attribute updates from the server to the client via session. <br/>
     * <i>It might be preferable to use sendAttributesToSessionTag() instead, for its ease of use.</i>
     *
     * @param attributes Collection of {@link Attribute} to send
     * @param session Specific session to send to.
     */
    public void sendAttributesToSession(Collection<Attribute> attributes, Session session) {
        sendAttributesToSessionIDs(attributes, Collections.singletonList(session.getId()));
    }

    /**
     * Send attribute updates from the server to the client via session id. <br/>
     * <i>It might be preferable to use sendAttributesToSessionTag() instead, for its ease of use.</i>
     *
     * @param attributes Collection of {@link Attribute} to send
     * @param sessionIds Specific session ids to send to. If a non-active session id is provided, no action is undertaken
     */
    public void sendAttributesToSessionIDs(Collection<Attribute> attributes, Collection<String> sessionIds) {
        if(attributes == null || attributes.isEmpty() || sessionIds == null || sessionIds.isEmpty()) {
            return;
        }

        sendAttributesToSession(attributes, sessionMemoryRepository.findSessionsByIds(sessionIds));
    }

    //TODO more or less same as in socket handler
    private void sendAttributesToSession(Collection<Attribute> attributes, List<Session> sessions) {
        sessions.parallelStream().filter(Objects::nonNull).forEach(session -> {
            final Session updatedSession = session.merge(attributes);
            List<Attribute> passThroughAttributes = updatedSession.findPassThroughAttributes();
            final Flux<DataBuffer> dataBufferFlux = renderer.render(session.getLastUsedTemplate(), updatedSession);
            final String oldHTML = updatedSession.getLastRenderedHTML();
            final String newHtml = FluxUtils.dataBufferFluxToString(dataBufferFlux);
            updatedSession.setLastRenderedHTML(newHtml);
            sessionMemoryRepository.store(updatedSession);
            updatedSession.getSink().push(mergeDiffs(diffEngine.calculate(oldHTML, newHtml), passThroughAttributes));
            updatedSession.setDepth(0);
        });
    }

    private Cache<String, Long> lastSessionSent = Caffeine.newBuilder()
            .maximumSize(2000L)
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();

    public void sendUploadCompletionPercentage(String attributeName, DataChunk dataChunk, Session session) {
        Object attribute = session.getAttribute(attributeName);
        if(attribute == null) {
            attribute = 0;
        }
        final double lastPercentage = Double.parseDouble(attribute.toString());
        if((dataChunk.getCompletion() - lastPercentage) > 1D || dataChunk.getCompletion() == 100D) {
            if(dataChunk.getCompletion() != 100D) {
                Long lastTimestamp = lastSessionSent.getIfPresent(session.getId());
                if(lastTimestamp == null || (System.currentTimeMillis() - lastTimestamp) > 500L) {
                    lastTimestamp = System.currentTimeMillis();
                    lastSessionSent.put(session.getId(), lastTimestamp);
                } else {
                    return;
                }
            }
            sendAttributesToSessionIDs(
                    Attribute.$$(attributeName, Math.round(dataChunk.getCompletion())),
                    Collections.singletonList(session.getId())
            );
        }
    }
}
