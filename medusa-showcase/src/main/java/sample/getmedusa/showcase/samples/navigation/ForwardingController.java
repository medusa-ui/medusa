package sample.getmedusa.showcase.samples.navigation;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@UIEventPage(path = "/detail/sample/forwarding", file = "/pages/sample/forwarding.html")
public class ForwardingController {

    private final ServerToClient serverToClient;

    public ForwardingController(ServerToClient serverToClient) {
        this.serverToClient = serverToClient;
    }

    public List<Attribute> forwardTo(String pathToForwardTo) {
        return List.of(new Attribute(StandardAttributeKeys.FORWARD, "/detail/" + pathToForwardTo));
    }

    public void registerMeForAForward(Session session) {
        sessionIds.add(session.getId());
    }

    private final List<String> sessionIds = new ArrayList<>();

    @Scheduled(fixedDelay = 500)
    public void triggerForwardFromSchedule() {
        for(String sessionId : sessionIds) {
            serverToClient.sendAttributesToSessionIDs(List.of(new Attribute(StandardAttributeKeys.FORWARD, "/detail/basic-button")), List.of(sessionId));
        }
        sessionIds.clear();
    }

}
