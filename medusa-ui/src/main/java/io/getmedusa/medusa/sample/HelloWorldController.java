package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;

@UIEventPage(path = "/", file = "/pages/hello-world")
public class HelloWorldController {

    private int counter = 0;

    public List<Attribute> setupAttributes() {
        return List.of(new Attribute("counterValue", counter));
    }

    public List<Attribute> increaseCounter() {
        return List.of(new Attribute("counterValue", ++counter));
    }

}
