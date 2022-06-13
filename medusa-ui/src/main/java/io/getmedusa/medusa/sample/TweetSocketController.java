package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.boot.RouteDetection;
import io.getmedusa.medusa.core.router.request.Route;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Map;

@Controller
public class TweetSocketController {

    public TweetSocketController(){
    }

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("event-emitter/{hash}")
    public Flux<Tweet> getByAuthor(final @Headers Map<String, Object> metadata, @Payload final Flux<SocketAction> request, @DestinationVariable String hash) {
        final Route route = RouteDetection.INSTANCE.findRoute(hash);
        return request.log().map(r -> {
            System.out.println(route.getControllerFQDN() + " // " + r.getAction());
            return new Tweet();
        });
    }

}