package io.getmedusa.medusa.core.injector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HTMLInjectorTest {

    @Test
    void test() {
        String result = HTMLInjector.INSTANCE.htmlStringInject("test.html", "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Hello Medusa</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>Hello Medusa</h1>\n" +
                "\n" +
                "<p>Counter: <span></span></p>\n" +
                "<button m-click=\"increaseCounter(2)\">Increase counter</button>\n" +
                "\n" +
                "</body>\n" +
                "</html>");

        System.out.println(result);

        Assertions.assertFalse(result.contains("m-click"));
        //Assertions.assertTrue(result.contains("var clientWebSocket = new WebSocket"));


    }

}
