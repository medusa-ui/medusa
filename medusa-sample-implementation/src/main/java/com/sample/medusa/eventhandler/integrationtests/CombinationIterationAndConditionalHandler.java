package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.ArrayList;
import java.util.List;

@UIEventPage(file = "pages/integration-tests/combo-if-conditional.html", path = "/test/combo-if-conditional")
public class CombinationIterationAndConditionalHandler {

    private static final String PHASE_A_DESC = "A (1, 2 / A, B / JANE, JOHN)";
    private static final String PHASE_B_DESC = "B (3, 4 / C, D / PETER, JEANETTE)";

    private List<Integer> integerList = new ArrayList<>(List.of(1, 2));
    private List<String> stringList = new ArrayList<>(List.of("A", "B"));
    private List<Person> personList = new ArrayList<>(List.of(new Person("1", "JANE"), new Person("2", "JOHN")));

    public PageAttributes setupAttributes(){
        return new PageAttributes()
                .with("integers", integerList)
                .with("strings", stringList)
                .with("persons", personList)
                .with("phase", PHASE_A_DESC);
    }

    //changes
    public DOMChanges changeToPhaseB() {
        integerList = new ArrayList<>(List.of(3, 4));
        stringList = new ArrayList<>(List.of("C", "D"));
        personList = new ArrayList<>(List.of(new Person("3", "PETER"), new Person("4", "JEANETTE")));

        return DOMChanges.of("integers", integerList)
                .and("strings", stringList)
                .and("persons", personList)
                .and("phase", PHASE_B_DESC);
    }

    public DOMChanges changeBackToPhaseA() {
        integerList = new ArrayList<>(List.of(1, 2));
        stringList = new ArrayList<>(List.of("A", "B"));
        personList = new ArrayList<>(List.of(new Person("1", "JANE"), new Person("2", "JOHN")));

        return DOMChanges.of("integers", integerList)
                .and("strings", stringList)
                .and("persons", personList)
                .and("phase", PHASE_A_DESC);
    }


}
