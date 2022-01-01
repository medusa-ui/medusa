package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.injector.tag.AbstractTag;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HydraURLReplacer extends AbstractTag {

    public static Set<String> STATIC_RESOURCES = new HashSet<>();

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        final String html = result.getHTML();
        return new InjectionResult(replaceUrls(html, request.headers()));
    }

    public String replaceUrls(String html, ServerRequest.Headers headers) {
        Integer hydraPath = findHydraPath(headers);
        if(null == hydraPath) return html;

        for(String staticResource : STATIC_RESOURCES) {
            html = html.replace("href=\"/"+staticResource+"\"", "href=\"/"+hydraPath+"/"+staticResource+"\"");
        }
        html = html.replace("/event-emitter/", "/" + hydraPath + "/event-emitter/");
        return html;
    }

    private Integer findHydraPath(ServerRequest.Headers headers) {
        if(headers == null) return null;
        List<String> relevantHeaders = headers.header("hydra-path");
        if(relevantHeaders.isEmpty()) return null;
        return Integer.parseInt(relevantHeaders.get(0));
    }

}
