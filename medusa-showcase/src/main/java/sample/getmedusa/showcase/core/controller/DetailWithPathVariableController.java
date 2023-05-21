package sample.getmedusa.showcase.core.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.springframework.web.reactive.function.server.ServerRequest;
import sample.getmedusa.showcase.core.Versions;
import sample.getmedusa.showcase.core.model.Component;

import java.util.List;

@UIEventPage(path = "/detail/pathvariable/{exampleValue}/{exampleValue2}", file = "/pages/detail")
public class DetailWithPathVariableController extends DetailController {

    @Override
    public List<Attribute> setupAttributes(ServerRequest request) {
        final Component component = Component.findComponent("pathvariable");
        final String title = component.getLabel();
        return List.of(new Attribute("title", title),
                new Attribute("type", "pathvariable"),
                new Attribute("serverCode", loadCode(component.getServerCode())),
                new Attribute("clientCode", loadCode(component.getClientCode())),
                new Attribute("version", Versions.getVersionFooter()));
    }

}
