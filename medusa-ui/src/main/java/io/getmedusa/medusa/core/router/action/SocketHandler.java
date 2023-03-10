package io.getmedusa.medusa.core.router.action;

import io.getmedusa.diffengine.Engine;
import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.RouteDetection;
import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.AttributeUtils;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.getmedusa.medusa.core.util.AttributeUtils.mergeDiffs;

//event emitter endpoint that rsocket can connect to

@Controller
public class SocketHandler {

    private final SessionMemoryRepository sessionMemoryRepository;
    private final ActionHandler actionHandler;
    private final Renderer renderer;
    private final Engine diffEngine;

    public SocketHandler(SessionMemoryRepository sessionMemoryRepository,
                         ActionHandler actionHandler,
                         Renderer renderer,
                         @Value("${medusa.allow-external-redirect:false}") boolean allowExternalRedirect){
        this.sessionMemoryRepository = sessionMemoryRepository;
        this.actionHandler = actionHandler;
        this.renderer = renderer;
        this.diffEngine = new Engine();
        AttributeUtils.setAllowExternalRedirect(allowExternalRedirect);
    }

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("event-emitter/{hash}/{sessionId}")
    public Flux<Set<ServerSideDiff>> eventEmitter(final @Headers Map<String, Object> metadata,
                                                  final @Payload Flux<SocketAction> request,
                                                  final @DestinationVariable String hash,
                                                  final @DestinationVariable String sessionId) {

        final Route route = RouteDetection.INSTANCE.findRoute(hash);

        //retrieve session
        //TODO can this be done via metadata/a more secure way?
        final Session session = sessionMemoryRepository.retrieve(sessionId, route);
        session.setInitialRender(false);

        request.onErrorReturn(new SocketAction())
                .map(r -> handleFileUploadIfRelated(r, session, route))
                .subscribe(r -> {
            if(!isUploadRelated(r)) {
                //execute action and combine attributes
                Session updatedSession = actionHandler.executeAndMerge(r, route, session);

                //not all attributes are used for rendering - some are pass-through, like redirections.
                //these get filtered out first
                List<Attribute> passThroughAttributes = updatedSession.findPassThroughAttributes();

                //render new HTML w/ new attributes
                final Flux<DataBuffer> dataBufferFlux = renderer.render(route.getTemplateHTML(), updatedSession);
                final String oldHTML = updatedSession.getLastRenderedHTML();
                final String newHtml = FluxUtils.dataBufferFluxToString(dataBufferFlux);
                updatedSession.setLastRenderedHTML(newHtml);
                sessionMemoryRepository.store(updatedSession);

                //run diff engine old HTML vs new
                updatedSession.getSink().push(mergeDiffs(diffEngine.calculate(oldHTML, newHtml), passThroughAttributes));
                updatedSession.setDepth(0);
            }
        });

        return session.getSink().asFlux();
    }

    private SocketAction handleFileUploadIfRelated(SocketAction r, Session session, Route route) {
        if(isUploadRelated(r)) {
            final UploadableUI bean = (UploadableUI) route.getController();
            final FileUploadMeta fileMeta = r.getFileMeta();
            final String fileId = fileMeta.getFileId();
            if("upload_start".equals(fileMeta.getsAct())) {
                session.getPendingFileUploads().put(fileId, fileMeta);
            } else if("upload_cancel".equals(fileMeta.getsAct())) {
                bean.onCancel(fileMeta,session);
            } else if("upload_error".equals(fileMeta.getsAct())) {
                bean.onError(fileMeta,session);
            } else {
                final FileUploadMeta originalMetadata = session.getPendingFileUploads().get(fileId);

                final boolean uploadCompleted = "upload_complete".equals(fileMeta.getsAct());
                if(uploadCompleted) {
                    fileMeta.setPercentage(100);
                }

                bean.uploadChunk(DataChunk.from(fileMeta, originalMetadata), session);

                if(uploadCompleted) {
                    session.getPendingFileUploads().remove(fileId);
                }
            }
        }
        return r;
    }

    public boolean isUploadRelated(SocketAction action) {
        return action.getFileMeta() != null;
    }
}