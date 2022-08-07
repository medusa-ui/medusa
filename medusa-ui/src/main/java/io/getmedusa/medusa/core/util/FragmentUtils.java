package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;

public class FragmentUtils {

    protected static final String NULL = "null";
    protected static final String SINGLE_QUOTE = "'";

    private FragmentUtils() {}

    public static String addFragmentRefToHTML(String html, String fragmentRef) {
        return html.replace(JSEventAttributeProcessor.FRAGMENT_REPLACEMENT, (fragmentRef == null) ? NULL : SINGLE_QUOTE + fragmentRef + SINGLE_QUOTE);
    }

}
