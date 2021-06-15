package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.io.IOException;
import java.util.Optional;


public class ReactiveWebSocketHandler implements WebSocketHandler {

    private UnicastProcessor<Event> messagePublisher;
    private Flux<String> outputMessages;
    private ObjectMapper mapper;

    public ReactiveWebSocketHandler(UnicastProcessor<Event> messagePublisher, Flux<Event> messages) {
        this.messagePublisher = messagePublisher;
        this.mapper = new ObjectMapper();
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.outputMessages = Flux.from(messages).map(this::toJSON);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        WebSocketMessageSubscriber subscriber = new WebSocketMessageSubscriber(messagePublisher);
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::toChatMessage)
                .subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete);
        return session.send(outputMessages.map(session::textMessage));
    }

    private static class WebSocketMessageSubscriber {
        private UnicastProcessor<Event> messagePublisher;
        private Optional<Event> lastReceivedMessage = Optional.empty();

        public WebSocketMessageSubscriber(UnicastProcessor<Event> messagePublisher) {
            this.messagePublisher = messagePublisher;
        }

        public void onNext(Event message) {
            System.out.println(message);
            lastReceivedMessage = Optional.of(message);
            messagePublisher.onNext(message);
        }

        public void onError(Throwable error) {
            error.printStackTrace();
        }

        public void onComplete() {
            lastReceivedMessage.ifPresent(messagePublisher::onNext);
        }
    }



    public Event toChatMessage(String json) {
        try {
            return mapper.readValue(json, Event.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON:" + json, e);
        }
    }

    public String toJSON(Event message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
