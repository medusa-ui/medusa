package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/multiple-variable-in-title", file = "pages/integration-tests/bug/multi-var-in-title.html")
public class MultipleVariableInTitleEventHandler {

    public PageAttributes setupAttributes() {
        return new PageAttributes()
                .with("value1", "메두사")
                .with("value2", "美杜莎");
    }

    public DOMChanges swapValues() {
        return of("value1", "美杜莎")
               .and("value2", "메두사");
    }

    public DOMChanges changeVarTwo(String val) {
        return of("value2", val);
    }
}
