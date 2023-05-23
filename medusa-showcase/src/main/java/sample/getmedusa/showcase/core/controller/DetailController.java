package sample.getmedusa.showcase.core.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.springframework.web.reactive.function.server.ServerRequest;
import sample.getmedusa.showcase.core.Versions;
import sample.getmedusa.showcase.core.model.Component;

import java.util.List;

@UIEventPage(path = "/detail/{type}", file = "/pages/detail")
public class DetailController extends AbstractController {

    public List<Attribute> setupAttributes(ServerRequest request) {
        final Component component = Component.findComponent(request.pathVariable("type"));
        final String title = component.getLabel();
        return List.of(new Attribute("title", title),
                new Attribute("type", request.pathVariable("type")),
                new Attribute("serverCode", loadCode(component.getServerCode())),
                new Attribute("clientCode", loadCode(component.getClientCode())),
                new Attribute("version", Versions.getVersionFooter()));
    }

}
