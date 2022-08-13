package sample.getmedusa.showcase.samples.live.statistic;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.session.StandardSessionTagKeys;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@UIEventPage(path = "/detail/sample/live-data", file = "/pages/sample/live-data.html")
public class LiveDataController {

    protected static final Random RANDOM = new Random();
    private final ServerToClient serverToClient;

    private final List<String> uniqueSessionsTagSubscribedForARandom = new ArrayList<>();
    private final List<String> uniqueSessionsIdSubscribedForARandom = new ArrayList<>();

    public LiveDataController(ServerToClient serverToClient) {
        this.serverToClient = serverToClient;
    }

    public List<Attribute> setupAttributes(Session session) {
        subscribeTag(session);
        subscribeViaID(session);

        return List.of(
            new Attribute("randomNumberShared", 0),
            new Attribute("randomNumberUniquePerSession", 0),
            new Attribute("randomNumberUniquePerGroup", 0)
        );
    }

    public void subscribeTag(Session session) {
        session.putTag("uniqueTag", UUID.randomUUID().toString());
        uniqueSessionsTagSubscribedForARandom.add(session.getTag("uniqueTag"));
    }

    public void subscribeViaID(Session session) {
        uniqueSessionsIdSubscribedForARandom.add(session.getId());
    }

    //this process does not have to be a timer, but because timers run on a separate thread outside of context like, this perfect to kick off the serverside call
    @Scheduled(fixedDelay = 500)
    public void aServerProcess() {
        //global
        serverToClient.sendAttributesToSessionTag(
                List.of(new Attribute("randomNumberShared", randomInt())),
                StandardSessionTagKeys.ROUTE,
                "/detail/live-data");

        //unique per session
        for(String sessionId : new ArrayList<>(uniqueSessionsIdSubscribedForARandom)) {
            serverToClient.sendAttributesToSessionIDs(
                    List.of(new Attribute("randomNumberUniquePerSession", randomInt())),
                    List.of(sessionId)
            );
        }

        //per group
        for(String subscriberTagValue : new ArrayList<>(uniqueSessionsTagSubscribedForARandom)) {
            serverToClient.sendAttributesToSessionTag(
                    List.of(new Attribute("randomNumberUniquePerGroup", randomInt())),
                    "uniqueTag",
                    subscriberTagValue);
        }

        uniqueSessionsTagSubscribedForARandom.clear();
        uniqueSessionsIdSubscribedForARandom.clear();
    }

    private int randomInt() {
        return RANDOM.nextInt(99999);
    }

}
