package io.getmedusa.medusa;

import io.getmedusa.medusa.core.annotation.DOMChanges;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import org.springframework.stereotype.Component;

@Component
public class ExampleEventPage implements UIEventPage {

    int counter;
    int calculated; //TODO should not be needed: calculated value

    public int getCounter() {
        return counter;
    }

    @DOMChanges({"counter","calculated"})
    public void increase(int value) {
        counter++;
    }

    public int getCalculated(){
        return 13 * counter;
    }

    //maybe we could use @PageSetup(path="/ui-event-page", page="") ?
    @Override
    public String getPath() {
        return "/counter";
    }
    @Override
    public String getHtmlFile() {
        return "ui-event-page";
    }
}
