package io.getmedusa.medusa.core.router.action;

import io.getmedusa.diffengine.Engine;
import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.RefDetection;
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
import reactor.core.publisher.Mono;

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
    @MessageMapping("event-emitter/{hash}/{sessionId}/{locale}")
    public Flux<Set<ServerSideDiff>> eventEmitter(final @Headers Map<String, Object> metadata,
                                                  final @Payload Flux<SocketAction> request,
                                                  final @DestinationVariable String hash,
                                                  final @DestinationVariable String sessionId,
                                                  final @DestinationVariable String locale) {

        final Route route = RouteDetection.INSTANCE.findRoute(hash);

        //retrieve session
        final Session session = sessionMemoryRepository.retrieve(sessionId, route);
        session.withLocale(locale).setInitialRender(false);

        request.doOnError(t -> onErrorReturnEmptyAction(session, route))
                .onErrorReturn(new SocketAction())
                .map(r -> handleFileUploadIfRelated(r, session, route))
                .subscribe(r -> {
            if(!isUploadRelated(r)) {
                //execute action and combine attributes
                Mono<Session> updatedSession = actionHandler.executeAndMerge(r, route, session);
                updatedSession.subscribe(s -> {
                    //not all attributes are used for rendering - some are pass-through, like redirections.
                    //these get filtered out first
                    List<Attribute> passThroughAttributes = s.findPassThroughAttributes();

                    //render new HTML w/ new attributes
                    final Flux<DataBuffer> dataBufferFlux = renderer.render(route.getTemplateHTML(), s);
                    final String oldHTML = s.getLastRenderedHTML();
                    final String newHtml = FluxUtils.dataBufferFluxToString(dataBufferFlux);
                    s.setLastRenderedHTML(newHtml);
                    sessionMemoryRepository.store(s);

                    //run diff engine old HTML vs new
                    s.getSink().push(mergeDiffs(diffEngine.calculate(oldHTML, newHtml), passThroughAttributes));
                    s.setDepth(0);
                });
            }
        });

        return session.getSink().asFlux();
    }

    private void onErrorReturnEmptyAction(Session session, Route route) {
        final Map<String, FileUploadMeta> pendingFileUploads = session.getPendingFileUploads();
        if(!pendingFileUploads.isEmpty()) {
            for (Map.Entry<String, FileUploadMeta> pendingUpload : pendingFileUploads.entrySet()) {
                UploadableUI bean = getUploadableUI(socketActionWithFragment(pendingUpload.getValue()), route);
                bean.onCancel(pendingUpload.getValue(), session);
            }
        }
    }

    private static SocketAction socketActionWithFragment(FileUploadMeta fileUploadMeta) {
        final SocketAction socketAction = new SocketAction();
        socketAction.setFragment(fileUploadMeta.getFragment());
        return socketAction;
    }

    private SocketAction handleFileUploadIfRelated(SocketAction r, Session session, Route route) {
        if(isUploadRelated(r)) {
            final UploadableUI bean = getUploadableUI(r, route);

            final FileUploadMeta fileMeta = r.getFileMeta();
            fileMeta.setFragment(r.getFragment());

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

    private static UploadableUI getUploadableUI(SocketAction r, Route route) {
        final UploadableUI bean;
        if(null != r.getFragment()) {
            final UIEventPageCallWrapper beanByRef = RefDetection.INSTANCE.findBeanByRef(r.getFragment());
            if(null != beanByRef) {
                bean = (UploadableUI) beanByRef.getController();
            } else {
                bean = (UploadableUI) route.getController();
            }
        } else {
            bean = (UploadableUI) route.getController();
        }
        return bean;
    }

    public boolean isUploadRelated(SocketAction action) {
        return action.getFileMeta() != null;
    }
}