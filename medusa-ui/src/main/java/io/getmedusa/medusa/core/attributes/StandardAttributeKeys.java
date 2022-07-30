package io.getmedusa.medusa.core.attributes;

import java.util.List;

public final class StandardAttributeKeys {

    private StandardAttributeKeys() {}

    public static final String FORWARD = "#$forward:";

    public static List<String> findAllPassThroughKeys() {
        return List.of(FORWARD);
    }
}
