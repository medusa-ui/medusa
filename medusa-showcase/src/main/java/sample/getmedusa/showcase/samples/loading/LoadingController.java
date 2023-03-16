package sample.getmedusa.showcase.samples.loading;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.session.StandardSessionTagKeys;

import java.util.List;

@UIEventPage(path = "/detail/sample/loading", file = "/pages/sample/loading.html")
public class LoadingController {

    private final ServerToClient serverToClient;

    public LoadingController(ServerToClient serverToClient) {
        this.serverToClient = serverToClient;
    }

    public void loadingLogic() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                //ignore
            }
            serverToClient.sendAttributesToSessionTag(
                    List.of(new Attribute(StandardAttributeKeys.LOADING, "loading-done")),
                    StandardSessionTagKeys.ROUTE,
                    "/detail/loading");
        }).start();
    }

}
