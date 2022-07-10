package sample.getmedusa.showcase.core.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ComponentTest {

    @Test
    void testURIPartCreation() {
        Component c = new Component("Multiple selection list");
        Assertions.assertEquals("Multiple selection list", c.getLabel());
        Assertions.assertEquals("multiple-selection-list", c.getUrlPart());

        c = new Component("Modal / Alert");
        Assertions.assertEquals("Modal / Alert", c.getLabel());
        Assertions.assertEquals("modal-alert", c.getUrlPart());
    }

}
