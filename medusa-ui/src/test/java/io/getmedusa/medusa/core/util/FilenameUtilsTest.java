package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FilenameUtilsTest {

    @Test
    void testNormalize() {
        Assertions.assertEquals("pages/example.html", FilenameUtils.normalize("pages/example.html"));
        Assertions.assertEquals("pages/example.html", FilenameUtils.normalize("/pages/example.html"));
        Assertions.assertEquals("pages/example.html", FilenameUtils.normalize("pages/example"));
        Assertions.assertEquals("pages/example.html", FilenameUtils.normalize("/pages/example"));

        Assertions.assertEquals("example.html", FilenameUtils.normalize("example"));
        Assertions.assertEquals("example.html", FilenameUtils.normalize("/example"));
        Assertions.assertEquals("example.html", FilenameUtils.normalize("/example.html"));
        Assertions.assertEquals("example.html", FilenameUtils.normalize("example.html"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> FilenameUtils.normalize(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FilenameUtils.normalize(" "));
    }

    @Test
    void testHTMLRemoveExtension() {
        Assertions.assertEquals("pages/example", FilenameUtils.removeHTMLExtension("pages/example"));
        Assertions.assertEquals("pages/example", FilenameUtils.removeHTMLExtension("pages/example.html"));
        Assertions.assertEquals("/example", FilenameUtils.removeHTMLExtension("/example.html"));
        Assertions.assertEquals("example", FilenameUtils.removeHTMLExtension("example.html"));
    }
}