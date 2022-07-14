package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

import static java.util.List.of;

@UIEventPage(path = "/test", file = "/pages/session-test")
public class SessionVariableController {

    public List<Attribute> setupAttributes(ServerRequest request) {
        return of(new Attribute("counter", 23));
    }

    public List<Attribute> updateCounter(Session session, int amount) {
        int counter = session.getAttribute("counter");
        counter += amount;
        return of(new Attribute("counter", counter));
    }

    public List<Attribute> reset() {
        return of(new Attribute("counter", 0));
    }

}
