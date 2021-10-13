package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.util.TestRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.getmedusa.medusa.core.websocket.ReactiveWebSocketHandler.MAPPER;

class HTMLInjectorTest {

    @Test
    void test() {
        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), null, """
                <body>

                <h1>Hello Medusa</h1>

                [$if(3 > 5)]
                <p>Counter is above 5</p>
                [$end if]
                <p>Counter: <span></span></p>
                <button m-click="increaseCounter(2)">Increase counter</button>

                </body>""");

        System.out.println(result);

        Assertions.assertFalse(result.contains("m-click"));
    }

    @Test
    void generateVariableMap() throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("exampleString", "Hello Mesuda");
        variables.put("exampleInteger", 12);
        String json = "_M.variables = " + MAPPER.writeValueAsString(variables) + ";";
        System.out.println(json);
    }

    @Test
    void testRequestPathMatcher() {
        final Set<String> possiblePaths = new HashSet<>(Arrays.asList("/1/{a}", "/1/{a}/{b}", "/1/{a}/{b}/{c}", "/2/{a}/{b}"));

        final Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("a", "X");
        pathVariables.put("b", "X");

        final String url = "/1/X/X";

        Assertions.assertEquals("/1/{a}/{b}", HTMLInjector.INSTANCE.findPath(url, possiblePaths, pathVariables));
    }
}
