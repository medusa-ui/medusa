package io.getmedusa.medusa.core.annotation;

import java.util.Map;

public interface UIEventController {
    PageSetup setupPage();
    /*
    default void resolved(PageSetup pageSetup){
        System.out.println("IN UIEventController.resolved: " + pageSetup.getPageVariables());
        for (Map.Entry<String, Object> p : pageSetup.getPageVariables().entrySet()) {
            setupPage().getPageVariables().replace(p.getKey(), p.getValue());
            System.out.println(p.getKey() + " = " + p.getValue());
        }

        System.out.println("OUT UIEventController.resolved: " + setupPage().getPageVariables());
    }
    */
}
