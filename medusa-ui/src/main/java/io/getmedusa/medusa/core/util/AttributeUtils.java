package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import io.getmedusa.medusa.core.router.request.Route;

import java.util.ArrayList;
import java.util.List;

public final class AttributeUtils {

    private static boolean allowExternalRedirect = false;

    private AttributeUtils() {}

    public static void setAllowExternalRedirect(boolean value) {
        allowExternalRedirect = value;
    }

    public static List<JSReadyDiff> mergeDiffs(List<JSReadyDiff> diffs, List<Attribute> passThroughAttributes) {
        if(passThroughAttributes != null && !passThroughAttributes.isEmpty()) {
            List<JSReadyDiff> extraDiffs = new ArrayList<>();
            for(Attribute attribute : passThroughAttributes) {
                if(attribute.name().equalsIgnoreCase(StandardAttributeKeys.FORWARD)) {
                    String url = attribute.value().toString();
                    if(!allowExternalRedirect && isExternalRedirect(url)) {
                        throw new IllegalArgumentException("Not allowed to redirect externally unless explicitly configured via 'medusa.allow-external-redirect', to prevent Server Side Request Forgery. This is currently not the case, hence this error. Relative URLS ('/hello', '../hello') are also allowed but must either start with a '.' or a '/'");
                    }
                    extraDiffs.add(JSReadyDiff.buildNewRedirect(url));
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
}
