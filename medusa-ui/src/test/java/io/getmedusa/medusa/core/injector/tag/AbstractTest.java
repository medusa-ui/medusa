package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.HTMLInjector;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.nodes.Document;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;
import java.util.Map;

public abstract class AbstractTest {

    protected final ServerRequest request = MockServerRequest.builder().build();

    protected Document inject(String html, Map<String, Object> variables) {
        InjectionResult injectionResult = new InjectionResult(html);
        List<Tag> tags = HTMLInjector.getTags();
        for(Tag tag : tags) {
            injectionResult = tag.inject(injectionResult, variables, request);
        }
        return injectionResult.getDocument();
    }

    protected void removeNonDisplayedElements(Document doc) {
        doc.getElementsByAttributeValue("style", "display:none;").remove();
        doc.getElementsByTag("template").remove();
    }

    protected static class Person {

        private final String name;

        public Person(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    protected static class ComplexObject {

        private final String exampleValue;

        public ComplexObject(String exampleValue) {
            this.exampleValue = exampleValue;
        }

        public String getExampleValue() {
            return exampleValue;
        }
    }

}
