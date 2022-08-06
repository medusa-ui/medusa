package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

@UIEventPage(path = "/fragment", file = "/pages/fragment-root")
public class FragmentRootController {

    public List<Attribute> setupAttributes(ServerRequest serverRequest) {
        return List.of(
                new Attribute("service", "self"),
                new Attribute("ref", "search-bar")
        );
    }

}
