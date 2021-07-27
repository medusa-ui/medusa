package io.getmedusa.medusa.core.router;

import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

public interface IRequestStreamHandler {

    public HandlerFunction<ServerResponse> handle(String script, String styling, String fileName);

}
