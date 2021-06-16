package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.injector.tag.ClickTag;
import io.getmedusa.medusa.core.injector.tag.InjectionResult;
import io.getmedusa.medusa.core.injector.tag.ValueTag;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum HTMLInjector {

    INSTANCE;

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private final ClickTag clickTag;
    private final ValueTag valueTag;
    HTMLInjector() {
        this.clickTag = new ClickTag();
        this.valueTag = new ValueTag();
    }

    public String inject(Resource html) {
        try {
            String htmlString = StreamUtils.copyToString(html.getInputStream(), CHARSET);
            return htmlStringInject(htmlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String htmlStringInject(String htmlString) {
        InjectionResult result = clickTag.inject(htmlString);
        result = valueTag.inject(result);
        return injectScript(result);
    }

    private String injectScript(InjectionResult html) {
        return html.replaceFinal("</body>",
                "<script>\n" +
                        "var clientWebSocket = new WebSocket(\"ws://localhost:8080/event-emitter\");\n" +
                        "clientWebSocket.onopen = function() {\n"+
                        "    console.log(\"clientWebSocket.onopen\", clientWebSocket);\n"+
                        "    console.log(\"clientWebSocket.readyState\", \"websocketstatus\");\n"+
                        "}\n"+
                        "clientWebSocket.onclose = function(error) {\n"+
                        "    console.log(\"clientWebSocket.onclose\", clientWebSocket, error);\n"+
                        "    events(\"Closing connection\");\n"+
                        "}\n"+
                        "clientWebSocket.onerror = function(error) {\n"+
                        "    console.log(\"clientWebSocket.onerror\", clientWebSocket, error);\n"+
                        "    events(\"An error occured\");\n"+
                        "}\n"+
                        "clientWebSocket.onmessage = function(message) {\n"+
                        "    console.log(\"clientWebSocket.onmessage\", clientWebSocket, message);\n" +
                        "    vR(JSON.parse(message.data));\n"+
                        "}\n"+
                        "function events(responseEvent) {\n"+
                        "    console.log(responseEvent);\n"+
                        "}"+
                "\n" +
                "function vR(e) { e.forEach(k => document.querySelectorAll(\"[from-value=\"+k.f+\"]\").forEach(function(e) { e.innerText = k.v; })); }\n" +
                "function sE(e) { clientWebSocket.send(e); }</script>\n" +
                "</body>");
    }
}
