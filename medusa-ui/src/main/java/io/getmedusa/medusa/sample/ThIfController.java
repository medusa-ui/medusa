package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

@UIEventPage(path = "/th-if",file = "/pages/loading")
public class ThIfController {
    private static final Logger logger = LoggerFactory.getLogger(ThIfController.class);
    boolean top = true;

    public List<Attribute> setupAttributes(ServerRequest serverRequest, Session session) {
        return List.of(
                new Attribute("top",true),
                new Attribute("bottom",true)
        );
    }

    public List<Attribute> change(){
        if(top) {
            top = false;
            return List.of(
                    new Attribute("top", true),
                    new Attribute("bottom", false)
            );
        } else {
            return List.of(
                    new Attribute("top",true),
                    new Attribute("bottom",true)
            );
        }

    }

}
