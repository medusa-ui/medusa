package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public interface Tag {

    InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request);

}
