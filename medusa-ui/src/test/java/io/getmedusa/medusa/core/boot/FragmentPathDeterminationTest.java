package io.getmedusa.medusa.core.boot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FragmentPathDeterminationTest {

    final String FULL_IN_JAR_PATH = "jar:file:/D:/workspace/medusa/medusa-showcase/target/showcase-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/pages/fragments/_global_loader.html";
    final String FULL_IN_APP_PATH = "D:/workspace/medusa/medusa-showcase/target/showcase-0.0.1-SNAPSHOT.jar/classes/pages/fragments/_global_loader.html";

    @Test
    void testInJar() {
        String path = parsePath(FULL_IN_JAR_PATH);
        Assertions.assertEquals("/pages/fragments/_global_loader.html", path);
    }

    @Test
    void testInApp() {
        String path = parsePath(FULL_IN_APP_PATH);
        Assertions.assertEquals("/pages/fragments/_global_loader.html", path);
    }

    private String parsePath(String rawPath) {
        rawPath = rawPath.substring(rawPath.indexOf("/classes") + 8);
        return rawPath.substring(rawPath.indexOf("/"));
    }

}
