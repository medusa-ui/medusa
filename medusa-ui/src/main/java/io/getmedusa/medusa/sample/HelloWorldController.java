package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;

@UIEventPage(path = "/", file = "/pages/hello-world")
public class HelloWorldController {

    int counter;

    public List<Attribute> setupAttributes() {
        counter = 3;
        return change();
    }

    public List<Attribute> change(){
        counter++;
        if (counter == 4) counter = 0;
        boolean top = counter != 1;
        boolean middle = counter != 2;
        boolean bottom = counter != 3;

        return List.of(
                new Attribute("counter", counter ),
                new Attribute("top",top ),
                new Attribute("middle", middle),
                new Attribute("bottom", bottom)
        );
    }
}
