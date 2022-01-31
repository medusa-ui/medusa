package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.registry.ActiveSessionRegistry;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UIEventPage(file = "pages/integration-tests/each-bidirectional.html", path = "/each-bidirectional")
public class EachEventHandler {
    public List<String> loop = new ArrayList<>();
    public boolean running = false;

    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        return new PageAttributes()
                .with("loop", loop)
                .with("running", running);
    }

    public DOMChanges start() {
        running = true;
        return DOMChanges.of("running", running);
    }

    public DOMChanges stop() {
        running = false;
        return DOMChanges.of("running", running);
    }

    public DOMChanges clear() {
        loop.clear();
        return DOMChanges.of("loop", loop);
    }

    @Scheduled(fixedRate = 1000)
    public void runUpdate() {
        if(running) {
            loop.add(UUID.randomUUID().toString());
            ActiveSessionRegistry.getInstance().sendToAll(DOMChanges.of("loop", loop));
        }
    }

    @Scheduled(fixedRate = 1000, initialDelay = 500)
    public void runUpdate2() {
        if(running) {
            loop.add(Integer.toString(new SecureRandom().nextInt()));
            ActiveSessionRegistry.getInstance().sendToAll(DOMChanges.of("loop", loop).build());
        }
    }
}
