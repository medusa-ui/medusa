package io.getmedusa.medusa.core.attributes;

import java.util.List;

public final class StandardAttributeKeys {

    private StandardAttributeKeys() {}

    public static final String FORWARD = "#$forward:";
    public static final String JS_FUNCTION = "#$js_function:";
    public static final String LOADING = "#$loading:";

    public static List<String> findAllPassThroughKeys() {
        return List.of(FORWARD, JS_FUNCTION, LOADING);
    }
}
