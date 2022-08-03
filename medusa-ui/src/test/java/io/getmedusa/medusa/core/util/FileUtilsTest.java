package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void testLoad() {
        final String loadedFile = FileUtils.load("/pages/sample.html");
        Assertions.assertNotNull(loadedFile);
        Assertions.assertTrue(loadedFile.contains("<title>Hello world</title>"));
    }

}
