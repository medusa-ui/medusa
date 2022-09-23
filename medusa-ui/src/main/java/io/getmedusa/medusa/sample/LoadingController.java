package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.List;

@UIEventPage(path = "/loading", file = "/pages/loading")
public class LoadingController {

    public List<Attribute> setupAttributes(ServerRequest serverRequest, Session session) {
        return new ArrayList<>();
    }

    public List<Attribute> loadingLogic() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            //ignore
        }
        return List.of(new Attribute(StandardAttributeKeys.LOADING, "loading-done"));
    }

}
