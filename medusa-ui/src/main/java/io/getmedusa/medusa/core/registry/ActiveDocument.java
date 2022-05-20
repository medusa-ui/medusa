package io.getmedusa.medusa.core.registry;

import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public class ActiveDocument {

    private final String path;
    private final String file;
    private final String lastRender;
    private final Map<String, Object> variables;
    private final ServerRequest request;

    public ActiveDocument(String file, String path, Map<String, Object> variables, String lastRender, ServerRequest request) {
        this.file = file;
        this.path = path;
        this.request = request;
        this.variables = variables;
        this.lastRender = lastRender;
    }

    public static ActiveDocument empty() {
        return new ActiveDocument(null, null, null, null, null);
    }

    public String getPath() {
        return path;
    }

    public ServerRequest getRequest() {
        return request;
    }

    public String getFile() {
        return file;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public String getLastRender() {
        return lastRender;
    }
}
