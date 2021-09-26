package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TemplateIDGeneratorTest {

    @Test
    void testSingle() {
        ForEachElement element = new ForEachElement("[$foreach $history]x[$end for]", "x");
        String id = IdentifierGenerator.generateTemplateID(element);
        Assertions.assertEquals(2, id.split("t-").length);
        Assertions.assertTrue(id.split("t-")[0].isEmpty());
    }

    @Test
    void testWithParent() {
        ForEachElement parent = new ForEachElement("[$foreach $parent][$foreach $child]x[$end for][$end for]", "[$foreach $child]x[$end for]");
        ForEachElement child = new ForEachElement("[$foreach $child]x[$end for]", "x");
        child.setParent(parent);

        String id = IdentifierGenerator.generateTemplateID(child);
        System.out.println(id);
    }

}
