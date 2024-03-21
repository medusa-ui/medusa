package io.getmedusa.medusa.sample;


//parent controller that provides the value to be imported

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;
import java.util.Random;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/import-fragment", file = "/pages/import-fragment")
public class ImportFragmentRootController {

    private static final Random RANDOM = new Random();

    public List<Attribute> setupAttributes() {
        return $$("rootValue", RANDOM.nextInt(999));
    }

}
