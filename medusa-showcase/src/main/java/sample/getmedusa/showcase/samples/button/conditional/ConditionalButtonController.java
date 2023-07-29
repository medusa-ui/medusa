package sample.getmedusa.showcase.samples.button.conditional;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/detail/sample/conditional-button", file = "/pages/sample/conditional-button.html")
public class ConditionalButtonController {

    public List<Attribute> setupAttributes() {
        return $$("condition", false);
    }

    public List<Attribute> checkCondition(int amount) {
        return $$("condition", amount % 2 == 0);
    }

    public void noAction() {}

}
