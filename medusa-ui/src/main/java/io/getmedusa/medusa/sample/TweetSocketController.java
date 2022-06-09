package io.getmedusa.medusa.sample;

import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Map;

@Controller
public class TweetSocketController {

    private final TweetService service;

    public TweetSocketController(TweetService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("tweets.by.author")
    public Flux<Tweet> getByAuthor(final @Headers Map<String, Object> metadata, @Payload final Flux<TweetRequest> request) {
        return request.log().map(r -> {
            System.out.println(metadata);
            return service.getByAuthor(r.getAuthor());
        });
    }

}