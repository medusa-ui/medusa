package io.getmedusa.medusa.core.registry;

import org.jsoup.nodes.Document;
import org.springframework.web.reactive.function.server.ServerRequest;

public class ActiveDocument {

    private final String path;

    private final String file;
    private final Document document;

    private final ServerRequest request;

    public ActiveDocument(String file, String path, Document document, ServerRequest request) {
        this.file = file;
        this.path = path;
        this.document = document;
        this.request = request;
    }

    public static ActiveDocument empty() {
        return new ActiveDocument(null, null, null, null);
    }

    public String getPath() {
        return path;
    }

    public Document getDocument() {
        return document;
    }

    public ServerRequest getRequest() {
        return request;
    }

    public String getFile() {
        return file;
    }
}
