package io.getmedusa.medusa.core.router;

import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Interface for stream handlers. The implementation of this will be the classes that determine how to resolve a route based on an incoming request.
 */
public interface IRequestStreamHandler {

    HandlerFunction<ServerResponse> handle(String script, String styling, String fileName);

    boolean hasSecurity();
}
