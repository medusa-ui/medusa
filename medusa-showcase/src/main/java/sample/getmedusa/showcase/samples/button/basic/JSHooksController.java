package sample.getmedusa.showcase.samples.button.basic;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$;
import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/detail/sample/js-hooks", file = "/pages/sample/js-hooks.html")
public class JSHooksController {

    protected static final String COUNTER = "counter";

    public List<Attribute> setupAttributes() {
        return $$(COUNTER, 0);
    }

    public List<Attribute> updateCounter(int amount, Session session) {
        int counter = session.getAttribute(COUNTER);
        counter += amount;
        return $$($(COUNTER, counter));
    }

    public List<Attribute> reset() {
        return List.of(new Attribute(COUNTER, 0));
    }

}
