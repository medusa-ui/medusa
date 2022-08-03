package sample.getmedusa.showcase.core.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.springframework.web.reactive.function.server.ServerRequest;
import sample.getmedusa.showcase.core.model.Component;

import java.util.List;

@UIEventPage(path = "/detail/{type}", file = "/pages/detail")
public class DetailController {

    public List<Attribute> setupAttributes(ServerRequest request) {
        final Component component = Component.findComponent(request.pathVariable("type"));
        final String title = component.getLabel();
        return List.of(new Attribute("title", title),
                new Attribute("serverCode", "public class HelloServerCode {}"),
                new Attribute("clientCode", "<html>Hello client code</html>"));
    }

}
