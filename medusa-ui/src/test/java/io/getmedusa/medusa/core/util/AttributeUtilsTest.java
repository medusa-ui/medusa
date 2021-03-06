package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import io.getmedusa.medusa.core.router.request.Route;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class AttributeUtilsTest {

    private final List<JSReadyDiff> diffs = new ArrayList<>(List.of(JSReadyDiff.buildNewRemoval("xyz")));

    @BeforeEach
    public void init() {
        Route.URIs.clear();
        AttributeUtils.setAllowExternalRedirect(false);
    }

    @Test
    void testMergeNullOrEmpty() {
        List<JSReadyDiff> mergedDiffs = AttributeUtils.mergeDiffs(diffs, null);
        Assertions.assertEquals(diffs, mergedDiffs);

        mergedDiffs = AttributeUtils.mergeDiffs(diffs, new ArrayList<>());
        Assertions.assertEquals(diffs, mergedDiffs);
    }

    @Test
    void testWRedirectAttributeExternalNotAllowed() {
        Attribute redirectAttribute = new Attribute(StandardAttributeKeys.FORWARD, "https://google.com");
        Assertions.assertThrows(IllegalArgumentException.class, () -> AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute)));
    }

    @Test
    void testWRedirectAttributeInternal() {
        Route.URIs.add("https://mysite.com");
        Attribute redirectAttribute = new Attribute(StandardAttributeKeys.FORWARD, "https://mysite.com/hello-world");
        List<JSReadyDiff> mergedDiffs = AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute));
        Assertions.assertEquals(diffs, mergedDiffs);
    }

    @Test
    void testWRedirectAttributeRelative() {
        Attribute redirectAttribute = new Attribute(StandardAttributeKeys.FORWARD, "/hello-world");
        List<JSReadyDiff> mergedDiffs = AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute));
        Assertions.assertEquals(diffs, mergedDiffs);

        redirectAttribute = new Attribute(StandardAttributeKeys.FORWARD, "../hello-world");
        mergedDiffs = AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute));
        Assertions.assertEquals(diffs, mergedDiffs);
    }

    @Test
    void testWRedirectAttributeExternalWhenAllowed() {
        AttributeUtils.setAllowExternalRedirect(true);
        Attribute redirectAttribute = new Attribute(StandardAttributeKeys.FORWARD, "https://google.com/");
        List<JSReadyDiff> mergedDiffs = AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute));
        Assertions.assertEquals(diffs, mergedDiffs);
    }

}
