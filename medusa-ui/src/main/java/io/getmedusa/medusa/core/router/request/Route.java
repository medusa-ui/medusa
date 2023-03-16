package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Route {

    private final String path;
    private final String templateHTML;
    private final UIEventPageCallWrapper controller;

    public static final Set<String> URIs = new HashSet<>();

    public Route(String path, String templateHTML, Object controller) {
        this.path = path;
        this.templateHTML = templateHTML;
        this.controller = new UIEventPageCallWrapper(controller);
    }

    public String getPath() {
        return path;
    }

    public String getTemplateHTML() {
        return templateHTML;
    }

    public String getControllerFQDN() {
        return controller.toFQDN();
    }

    public Object getController() {
        return controller.getController();
    }

    public List<Attribute> getSetupAttributes(ServerRequest request, Session session) {
        return controller.setupAttributes(request, session);
    }

    public String generateHash() {
        return UriUtils.encode(getPath(), StandardCharsets.UTF_8);
    }
}
