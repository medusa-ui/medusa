package io.getmedusa.medusa.core.injector;

import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HydraURLReplacer {

    public static Set<String> STATIC_RESOURCES = new HashSet<>();

    public String inject(String html, ServerRequest request) {
        return replaceUrls(html, request.headers());
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
