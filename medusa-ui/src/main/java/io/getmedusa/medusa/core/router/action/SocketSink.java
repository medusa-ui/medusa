package io.getmedusa.medusa.core.router.action;

import reactor.core.publisher.Flux;

import java.util.List;
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
        void register(EventListener<List<JSReadyDiff>> eventListener);

        void dataChunk(List<JSReadyDiff> value);
        void processComplete();
    }

    private final EventProcessor eventProcessor = new EventProcessor() {

        private EventListener<List<JSReadyDiff>> eventListener;
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        @Override
        public void register(EventListener<List<JSReadyDiff>> eventListener) {
            this.eventListener = eventListener;
        }

        @Override
        public void dataChunk(List<JSReadyDiff> value) {
            executor.schedule(() -> eventListener.onDataChunk(value), 0, TimeUnit.MILLISECONDS);
        }

        @Override
        public void processComplete() {
            executor.schedule(eventListener::processComplete, 500, TimeUnit.MILLISECONDS);
        }
    };

    Flux<List<JSReadyDiff>> eventFlux = Flux.create(sink -> eventProcessor.register(
            new EventListener<>() {
                public void onDataChunk(List<JSReadyDiff> chunk) {
                    sink.next(chunk);
                }

                public void processComplete() {
                    sink.complete();
                }
            }));

    public void push(List<JSReadyDiff> heartbeat) {
        eventProcessor.dataChunk(heartbeat);
    }

    public Flux<List<JSReadyDiff>> asFlux() {
        return eventFlux;
    }

}