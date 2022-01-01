package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/multiple-variable-in-title", file = "pages/integration-tests/bug/multi-var-in-title.html")
public class MultipleVariableInTitleEventHandler {

    public PageAttributes setupAttributes() {
        final String value1 = "메두사";
        final String value2 = "美杜莎";
        return new PageAttributes()
                .with("title", "Medusa in other languages : " + value1 + " " + value2)
                .with("value1", value1)
                .with("value2", value2);
    }

    public DOMChanges swapValues() {
        final String value1 = "美杜莎";
        final String value2 = "메두사";

        return of("value1", value1)
               .and("value2", value2)
                .and("title", "Medusa in other languages : " + value1 + " " + value2);
    }

    public DOMChanges changeVarTwo(String val) {
        return of("value2", val).and("title", "Medusa in other languages : 메두사 XYZ");
    }
}
