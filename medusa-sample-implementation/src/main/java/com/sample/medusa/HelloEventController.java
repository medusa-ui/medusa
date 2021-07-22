package com.sample.medusa;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.annotation.UIEventController;
import org.springframework.stereotype.Component;

@Component
public class HelloEventController implements UIEventController {

    @Override
    public PageSetup setupPage() {
        return new PageSetup("/hello/{who}","pages/hello")
                .with("base", "Welcome")
                .withPathVariable("who","who") // <-- get actual value from the path-variable {who} and store it
                ;
    }
}
