package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.session.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.getmedusa.medusa.core.boot.StaticResourcesDetection.INSTANCE;

class StaticResourceDetectionTest {

    final Session session = new Session("123");

    @ParameterizedTest
    @CsvSource({
            "<script src=\"/websocket.js\"></script>, " +
                    "/static/websocket.js, " +
                    "<script src=\"/123/websocket.js\"></script>",

            "<script src=\"/somefolder/333/websocket.js\"></script>, " +
                    "/static/somefolder/333/websocket.js, " +
                    "<script src=\"/123/somefolder/333/websocket.js\"></script>",

            "<script src=\"somefolder/333/websocket.js\"></script>, " +
                    "/static/somefolder/333/websocket.js, " +
                    "<script src=\"/123/somefolder/333/websocket.js\"></script>",

            "<link rel=\"stylesheet\" href=\"mystyle.css\">, " +
                    "/static/mystyle.css, " +
                    "<link rel=\"stylesheet\" href=\"/123/mystyle.css\">"
    })
    void testReplaceScriptTag(String scriptTag, String staticResource, String expectedResult) {
        INSTANCE.testLoadStaticResource(staticResource);
        INSTANCE.detectUsedResources(scriptTag);
        Assertions.assertEquals(expectedResult, INSTANCE.prependStatisUrlsWithHydraPath(scriptTag, session));
    }

    @Test
    void testReplaceWithWebsocketByDefault() {
        final String scriptTag = "<script src=\"/websocket.js\"></script>";
        INSTANCE.detectUsedResources(scriptTag);
        Assertions.assertEquals("<script src=\"/123/websocket.js\"></script>", INSTANCE.prependStatisUrlsWithHydraPath(scriptTag, session));
    }
}
