package sample.getmedusa.showcase.samples.reactive;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/detail/sample/reactive", file = "/pages/sample/reactive.html")
public class ReactiveController {

    public Mono<List<Attribute>> setupAttributes() {
        return Mono.just(0)
                .map(i -> $$("counter", i));
    }

    public Mono<List<Attribute>> updateCounter(int amount, Session session) {
        Flux<List<Attribute>> listA = Flux.just($$("listA_valueA", 1, "listA_valueB", 2));

        int counter = session.getAttribute("counter");
        counter += amount;
        final Mono<List<Attribute>> listB = Mono.just($$("counter", counter));

        return FluxUtils.join(listA, listB);
    }
}
