package io.getmedusa.medusa;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChange;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExampleEventHandler implements UIEventController {

    private int counter = 0;
    private final List<String> listOfItemsBought = new ArrayList<>();

    @Override
    public PageSetup setupPage() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("counter-value", counter);
        modelMap.put("last_bought", "Nothing yet!");
        modelMap.put("items-bought", listOfItemsBought);
        modelMap.put("search", "initial value!");
        return new PageSetup(
                "/",
                "hello-world",
                modelMap);
    }

    public List<DOMChange> increaseCounter(Integer parameter) {
        counter += parameter;
        if(counter > 10) {
            counter = 0;
        }

        return Collections.singletonList(new DOMChange("counter-value", counter));
    }

    public List<DOMChange> buy(Object... parameters) {
        StringBuilder itemsBought = new StringBuilder();
        String appender = "";
        for(Object param : parameters) {
            itemsBought.append(appender);
            itemsBought.append(param.toString());
            appender = ", ";
        }
        listOfItemsBought.add(itemsBought.toString());
        return Arrays.asList(
                new DOMChange("items-bought", listOfItemsBought),
                new DOMChange("last_bought", itemsBought.toString()));
    }

    public List<DOMChange> search() {
        return Collections.singletonList(new DOMChange("search", UUID.randomUUID().toString()));
    }
}
