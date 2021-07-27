package io.getmedusa.medusa.core.router;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.getmedusa.medusa.core.injector.HTMLInjector.INSTANCE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@ConditionalOnMissingBean(RequestStreamHandlerWithSecurity.class)
public class RequestStreamHandler implements IRequestStreamHandler {

    @Override
    public HandlerFunction<ServerResponse> handle(String script, String styling, String fileName) {
        return request ->ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(request, null, fileName, script, styling));
    }

}
