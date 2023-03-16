package io.getmedusa.medusa.core.boot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class RefDetectionTest {

    private final String templateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="http://www.getmedusa.io/">
            <body>
                <p>Value: <span th:text="${counter}"></span></p>
                
                <div m:ref="checkout1">
                    This piece of code can be used in another app.
                    <p>Value: <span th:text="${counter}"></span></p>
                    End of code.
                </div>
                
                <section m:ref="checkout2">
                    This piece of code can be used in another app.
                    <p>Value: <span th:text="${counter}"></span></p>
                    End of code.
                </section>
                
                <button m:action="increment()">increment</button>
            </body>
            </html>
            """;

    @Test
    void testFindingReferences() {
        Map<String, String> refs = RefDetection.INSTANCE.findRefs("<html></html>");
        Assertions.assertEquals(0, refs.entrySet().size());

        refs = RefDetection.INSTANCE.findRefs(templateHTML);
        Assertions.assertEquals(2, refs.entrySet().size());
        final List<String> refNames = refs.keySet().stream().sorted().toList();
        Assertions.assertEquals("[checkout1, checkout2]", refNames.toString());
    }

}
