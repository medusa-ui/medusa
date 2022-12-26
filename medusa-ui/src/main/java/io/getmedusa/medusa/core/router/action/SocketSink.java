package io.getmedusa.medusa.core.router.action;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import reactor.core.publisher.Flux;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A sink is a never ending connection from the server's perspective (ie until the client kills it)
 * It can be used to post ad-hoc replies to the client
 */
public class SocketSink {

    interface EventListener<T> {
        void onDataChunk(T chunk);
        void processComplete();
    }

    interface EventProcessor {
        void register(EventListener<Set<ServerSideDiff>> eventListener);

        void dataChunk(Set<ServerSideDiff> value);
        void processComplete();
    }

    private final EventProcessor eventProcessor = new EventProcessor() {

        private EventListener<Set<ServerSideDiff>> eventListener;
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        @Override
        public void register(EventListener<Set<ServerSideDiff>> eventListener) {
            this.eventListener = eventListener;
        }

        @Override
        public void dataChunk(Set<ServerSideDiff> value) {
            executor.schedule(() -> eventListener.onDataChunk(value), 0, TimeUnit.MILLISECONDS);
        }

        @Override
        public void processComplete() {
            executor.schedule(eventListener::processComplete, 500, TimeUnit.MILLISECONDS);
        }
    };

    Flux<Set<ServerSideDiff>> eventFlux = Flux.create(sink -> eventProcessor.register(
            new EventListener<>() {
                public void onDataChunk(Set<ServerSideDiff> chunk) {
                    sink.next(chunk);
                }

                public void processComplete() {
                    sink.complete();
                }
            }));

    public void push(Set<ServerSideDiff> heartbeat) {
        eventProcessor.dataChunk(heartbeat);
    }

    public Flux<Set<ServerSideDiff>> asFlux() {
        return eventFlux;
    }

    public SocketSink() {
        this.eventProcessor.dataChunk(new LinkedHashSet<>());
    }

}