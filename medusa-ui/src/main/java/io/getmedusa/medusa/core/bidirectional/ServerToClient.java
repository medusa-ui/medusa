package io.getmedusa.medusa.core.bidirectional;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.diffengine.DiffEngine;
import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Exposed the ability to easily send {@link Attribute} from server to client.
 */
@Component
public class ServerToClient {

    private final SessionMemoryRepository sessionMemoryRepository;
    private final Renderer renderer;
    private final DiffEngine diffEngine;

    public ServerToClient(SessionMemoryRepository sessionMemoryRepository, Renderer renderer, DiffEngine diffEngine) {
        this.sessionMemoryRepository = sessionMemoryRepository;
        this.renderer = renderer;
        this.diffEngine = diffEngine;
    }

    /**
     * Send attribute updates from the server to the client via session tag.
     *
     * @param attributes Collection of {@link Attribute} to send
     * @param sessionTag See StandardSessionTagKeys or use a custom tag (Required)
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
            final Flux<DataBuffer> dataBufferFlux = renderer.render(session.getLastUsedTemplate(), updatedSession);
            final String oldHTML = updatedSession.getLastRenderedHTML();
            final String newHtml = FluxUtils.dataBufferFluxToString(dataBufferFlux);
            updatedSession.setLastRenderedHTML(newHtml);
            sessionMemoryRepository.store(updatedSession);
            updatedSession.getSink().push(diffEngine.findDiffs(oldHTML, newHtml));
        });
    }
}
