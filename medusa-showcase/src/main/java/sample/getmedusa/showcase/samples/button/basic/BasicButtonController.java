package sample.getmedusa.showcase.samples.button.basic;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;

import java.util.List;

@UIEventPage(path = "/detail/sample/basic-button", file = "/pages/sample/basic-button.html")
public class BasicButtonController {

    protected static final String COUNTER = "counter";

    public List<Attribute> setupAttributes() {
        return List.of(new Attribute(COUNTER, 0));
    }

    public List<Attribute> updateCounter(int amount, Session session) {
        int counter = session.getAttribute(COUNTER);
        counter += amount;
        return List.of(new Attribute(COUNTER, counter));
    }

    public List<Attribute> reset() {
        return List.of(new Attribute(COUNTER, 0));
    }

}
