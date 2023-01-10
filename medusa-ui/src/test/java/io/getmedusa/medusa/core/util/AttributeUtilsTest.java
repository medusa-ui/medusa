package io.getmedusa.medusa.core.util;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.router.request.Route;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static io.getmedusa.medusa.core.attributes.Attribute.$;
import static io.getmedusa.medusa.core.attributes.Attribute.$$;

class AttributeUtilsTest {

    private final Set<ServerSideDiff> diffs = new LinkedHashSet<>();

    @BeforeEach
    public void init() {
        Route.URIs.clear();
        AttributeUtils.setAllowExternalRedirect(false);
    }

    @Test
    void testMergeNullOrEmpty() {
        Set<ServerSideDiff> mergedDiffs = AttributeUtils.mergeDiffs(diffs, null);
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
        Set<ServerSideDiff> mergedDiffs = AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute));
        Assertions.assertEquals(diffs, mergedDiffs);
    }

    @Test
    void testWRedirectAttributeRelative() {
        Attribute redirectAttribute = new Attribute(StandardAttributeKeys.FORWARD, "/hello-world");
        Set<ServerSideDiff> mergedDiffs = AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute));
        Assertions.assertEquals(diffs, mergedDiffs);

        redirectAttribute = new Attribute(StandardAttributeKeys.FORWARD, "../hello-world");
        mergedDiffs = AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute));
        Assertions.assertEquals(diffs, mergedDiffs);
    }

    @Test
    void testWRedirectAttributeExternalWhenAllowed() {
        AttributeUtils.setAllowExternalRedirect(true);
        Attribute redirectAttribute = new Attribute(StandardAttributeKeys.FORWARD, "https://google.com/");
        Set<ServerSideDiff> mergedDiffs = AttributeUtils.mergeDiffs(diffs, List.of(redirectAttribute));
        Assertions.assertEquals(diffs, mergedDiffs);
    }

    @Test
    void testKeyPairs() {
        List<Attribute> list = $$("x", 1, "y", 2);

        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("x", list.get(0).name());
        Assertions.assertEquals("y", list.get(1).name());
        Assertions.assertEquals(1, list.get(0).value());
        Assertions.assertEquals(2, list.get(1).value());
    }

    @Test
    void testKeyPairsWithAttributes() {
        List<Attribute> list = $$($("x", 1), $("y", 2));

        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("x", list.get(0).name());
        Assertions.assertEquals("y", list.get(1).name());
        Assertions.assertEquals(1, list.get(0).value());
        Assertions.assertEquals(2, list.get(1).value());
    }

}
