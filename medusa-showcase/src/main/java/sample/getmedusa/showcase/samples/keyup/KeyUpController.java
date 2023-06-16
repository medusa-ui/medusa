package sample.getmedusa.showcase.samples.keyup;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/detail/sample/keyup", file = "/pages/sample/keyup.html")
public class KeyUpController {

    public List<Attribute> setupAttributes() {
        return $$("onEnterValue", "-",
                "onSpaceValue", "-");
    }

    public List<Attribute> sendTextOnSpace(String value) {
        return List.of(new Attribute("onSpaceValue", value.trim()));
    }
    public List<Attribute> sendTextOnEnter(String value, Session session) {
        return List.of(new Attribute("onEnterValue", value));
    }

}
