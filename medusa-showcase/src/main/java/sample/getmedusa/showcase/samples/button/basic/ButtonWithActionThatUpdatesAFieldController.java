package sample.getmedusa.showcase.samples.button.basic;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import sample.getmedusa.showcase.samples.AbstractSampleController;

import java.util.List;

@UIEventPage(path = "/detail/sample/basic-button", file = "/pages/sample/basic-button.html")
public class ButtonWithActionThatUpdatesAFieldController extends AbstractSampleController {

    protected static final String COUNTER = "counter";

    public List<Attribute> setupAttributes() {
        return List.of( new Attribute("pageCode", pageCodeAsString(this)),
                        new Attribute("controllerCode", controllerCodeAsString(this)),
                        new Attribute(COUNTER, 0));
    }

    public List<Attribute> updateCounter(int amount, Session session) {
        int counter = session.getAttribute(COUNTER, Integer.class);
        counter += amount;
        return List.of(new Attribute(COUNTER, counter));
    }

    public List<Attribute> reset() {
        return List.of(new Attribute(COUNTER, 0));
    }

}
