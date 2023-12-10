package io.getmedusa.medusa.sample;


//parent controller that provides the value to be imported

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;
import java.util.Random;

@UIEventPage(path = "/import-fragment", file = "/pages/import-fragment")
public class ImportFragmentRootController {

    private static final Random RANDOM = new Random();

    public List<Attribute> setupAttributes(ServerRequest serverRequest) {
        return List.of(
                new Attribute("rootValue", RANDOM.nextInt(999))
        );
    }

}
