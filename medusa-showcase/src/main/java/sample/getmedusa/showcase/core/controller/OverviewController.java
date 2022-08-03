package sample.getmedusa.showcase.core.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import sample.getmedusa.showcase.core.model.Component;

import java.util.List;

@UIEventPage(path = "/", file = "/pages/overview")
public class OverviewController {

    public List<Attribute> setupAttributes() {
        return List.of(new Attribute("categoryMap", Component.MAP));
    }

}
