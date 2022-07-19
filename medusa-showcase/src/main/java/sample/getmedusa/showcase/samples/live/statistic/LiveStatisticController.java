package sample.getmedusa.showcase.samples.live.statistic;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.session.StandardSessionTagKeys;
import org.springframework.scheduling.annotation.Scheduled;
import sample.getmedusa.showcase.samples.AbstractSampleController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@UIEventPage(path = "/detail/sample/live-statistic", file = "/pages/sample/live-statistic.html")
public class LiveStatisticController extends AbstractSampleController {

    protected static final Random RANDOM = new Random();
    private final ServerToClient serverToClient;

    private final List<String> uniqueSessionsSubscribedForARandom = new ArrayList<>();

    public LiveStatisticController(ServerToClient serverToClient) {
        this.serverToClient = serverToClient;
    }

    public List<Attribute> setupAttributes(Session session) {
        subscribe(session);

        return List.of(
                new Attribute("pageCodeShared", pageCodeAsString(this, "samples.live.statistic.Shared")),
                new Attribute("controllerCodeShared", controllerCodeAsString(this, "samples.live.statistic.Shared")),

                new Attribute("pageCodePerSession", pageCodeAsString(this, "samples.live.statistic.PerSession")),
                new Attribute("controllerCodePerSession", controllerCodeAsString(this, "samples.live.statistic.PerSession")),

                new Attribute("pageCodePerGroup", pageCodeAsString(this, "samples.live.statistic.PerGroup")),
                new Attribute("controllerCodePerGroup", controllerCodeAsString(this, "samples.live.statistic.PerGroup")),

                new Attribute("randomNumberShared", 0),
                new Attribute("randomNumberUniquePerSession", 0),
                new Attribute("randomNumberUniquePerGroup", 0)
            );

    }

    public void subscribe(Session session) {
        session.putTag("uniqueTag", UUID.randomUUID().toString());
        uniqueSessionsSubscribedForARandom.add(session.getTag("uniqueTag"));
    }

    //this process does not have to be a timer, but because timers run on a separate thread outside of context like, this perfect to kick off the serverside call
    @Scheduled(fixedDelay = 2000)
    public void aServerProcess() {
        serverToClient.sendAttributesToSessionTag(
                List.of(new Attribute("randomNumberShared", RANDOM.nextInt(99999))),
                StandardSessionTagKeys.ROUTE,
                "/detail/sample/live-statistic");

        for(String subscriberTagValue : uniqueSessionsSubscribedForARandom) {
            serverToClient.sendAttributesToSessionTag(
                    List.of(new Attribute("randomNumberUniquePerSession", RANDOM.nextInt(99999))),
                    "uniqueTag",
                    subscriberTagValue);
        }
        uniqueSessionsSubscribedForARandom.clear();
    }

}
