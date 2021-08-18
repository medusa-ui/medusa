package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/non-latin-characters", file = "pages/integration-tests/bug/non-latin-characters.html")
public class NonLatinCharactersEventHandler {

    public PageAttributes setupAttributes() {
        return new PageAttributes()
                .with("value1", "메두사")
                .with("value2", "美杜莎")
                .with("passed-value", "")
                .with("passed-value-input", "");
    }

    public DOMChanges swapValues() {
        return of("value1", "美杜莎")
               .and("value2", "메두사");
    }

    public DOMChanges passValue(String value1) {
        return of("passed-value", value1);
    }

    public DOMChanges passValueInput(String passValueInput){
        return of("passed-value-input", passValueInput);
    }
}
