package io.getmedusa.medusa.core.util;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.router.request.Route;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AttributeUtils {

    private static boolean allowExternalRedirect;

    private AttributeUtils() {}

    public static void setAllowExternalRedirect(boolean value) {
        allowExternalRedirect = value;
    }

    public static Set<ServerSideDiff> mergeDiffs(Set<ServerSideDiff> diffs, List<Attribute> passThroughAttributes) {
        if(passThroughAttributes != null && !passThroughAttributes.isEmpty()) {
            Set<ServerSideDiff> extraDiffs = new LinkedHashSet<>();
            for(Attribute attribute : passThroughAttributes) {
                if(attribute.name().equalsIgnoreCase(StandardAttributeKeys.FORWARD)) {
                    String url = attribute.value().toString();
                    if(!allowExternalRedirect && isExternalRedirect(url)) {
                        throw new IllegalArgumentException("Not allowed to redirect externally unless explicitly configured via 'medusa.allow-external-redirect', to prevent Server Side Request Forgery. This is currently not the case, hence this error. Relative URLS ('/hello', '../hello') are also allowed but must either start with a '.' or a '/'");
                    }
                    extraDiffs.add(ServerSideDiff.buildNewRedirect(url));
                } else if(attribute.name().equalsIgnoreCase(StandardAttributeKeys.JS_FUNCTION)) {
                    extraDiffs.add(ServerSideDiff.buildNewJSFunction(attribute.value().toString()));
                } else if(attribute.name().equalsIgnoreCase(StandardAttributeKeys.LOADING)) {
                    extraDiffs.add(ServerSideDiff.buildNewLoading(attribute.value().toString()));
                } else if(attribute.name().equalsIgnoreCase(StandardAttributeKeys.VALIDATION)) {
                    extraDiffs.addAll((List<ServerSideDiff>) attribute.value());
                }
            }
            diffs.addAll(extraDiffs);
        }
        return diffs;
    }

    private static boolean isExternalRedirect(String url) {
        return !isRelativeUrl(url) && !isInternalUrl(url);
    }

    private static boolean isInternalUrl(String url) {
        for(String uri : Route.URIs) {
            if(url.startsWith(uri)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRelativeUrl(String url) {
        return url.startsWith(".") || url.startsWith("/");
    }

    public static Map<String, Object> toLastParameterMap(List<Attribute> attributes) {
        return attributes.stream().collect(Collectors.toMap(Attribute::name, Attribute::value, (a, b) -> b));
    }
}
