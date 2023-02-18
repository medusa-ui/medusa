package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoaderUtilsTest {

    @Test
    void testParseGlobalLoaderNoWrapper() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> LoaderUtils.parseGlobalLoader("<div>Error</div>"));
    }

    @Test
    void testParseGlobalLoader() {
        final String globalLoader = """
                <div id="m-full-loader">
                    <h1>Loading ...</h1>
                </div>
                """;
        Assertions.assertTrue(LoaderUtils.parseGlobalLoader(globalLoader).contains("div id=\"m-full-loader\" style=\"display: none;\""));
    }

    @Test
    void testParseButtonLoader() {
        final String buttonLoader = """
                <div class="m-button-loader">
                    <h1>Loading ...</h1>
                </div>
                <style>color: red;</style>
                """;
        final String parsed = LoaderUtils.parseButtonLoader(buttonLoader);
        System.out.println(parsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertTrue(parsed.contains("<style>color: red;</style>"));
        Assertions.assertTrue(parsed.contains("</template>"));
    }

}
