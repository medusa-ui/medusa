package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.nodes.Document;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public abstract class AbstractTest {

    protected static final IterationTag ITERATION_TAG = new IterationTag();
    protected static final ValueTag VALUE_TAG = new ValueTag();
    protected static final ConditionalTag CONDITIONAL_TAG = new ConditionalTag();
    protected static final ClickTag CLICK_TAG = new ClickTag();

    protected final ServerRequest request = MockServerRequest.builder().build();

    protected Document inject(String html, Map<String, Object> variables) {
        return CLICK_TAG.inject(
                    CONDITIONAL_TAG.inject(
                        VALUE_TAG.inject(
                                ITERATION_TAG.inject(new InjectionResult(html),
                                    variables, request),
                            variables, request),
                    variables, request),
                variables, request).getDocument();
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
