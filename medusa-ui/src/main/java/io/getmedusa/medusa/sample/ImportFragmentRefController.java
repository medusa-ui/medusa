package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

//ref fragment which actually imports the variable
@UIEventPage(path = "/fragments/import-ref", file = "/pages/fragments/import-fragment-ref")
public class ImportFragmentRefController {

    public List<Attribute> setupAttributes() {
        return $$("counter", 0);
    }

    public List<Attribute> updateCounter(int amount, Session session) {
        int counter = session.getAttribute("counter");
        counter += amount;
        return $$("counter", counter);
    }

}
