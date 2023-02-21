package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class MethodDetectionTest {
    MethodDetection methodDetection = MethodDetection.INSTANCE;

    @Test
    void testCorrectTestController() {
        methodDetection.consider(new CorrectTestController());
        String beanName = CorrectTestController.class.getName();

        Assertions.assertTrue(methodDetection.shouldBeginWithSession(beanName, "sessionFirst"));
        Assertions.assertFalse(methodDetection.shouldEndWithSession(beanName, "sessionFirst"));

        Assertions.assertTrue(methodDetection.shouldEndWithSession(beanName, "sessionLast"));
        Assertions.assertFalse(methodDetection.shouldBeginWithSession(beanName, "sessionLast"));

        Assertions.assertTrue(methodDetection.shouldBeginWithSession(beanName, "session"));
        Assertions.assertFalse(methodDetection.shouldEndWithSession(beanName, "session"));

    }

    @Test
    void testBadTestController() {
        RuntimeException ex = Assertions.assertThrows(
                            IllegalArgumentException.class,
                            () -> methodDetection.consider(new BadTestController()));

        Assertions.assertEquals(
                "'io.getmedusa.medusa.core.boot.BadTestController' " +
                        "has multiple callable methods named 'callable' that could be mapped to a Medusa action. " +
                        "All callable method names must be unique.",
                ex.getMessage());
    }

}

/* TestControllers */
@UIEventPage(path = "", file = "/pages/sample")
class CorrectTestController {

    public List<Attribute> sessionFirst(Session session, String something){
        return session(session, something);
    }

    public List<Attribute> sessionLast(String something, Session session ){
        return session(session, something);
    }

    public List<Attribute> session(Session session ){
        return session(session, "");
    }
    /* private methods are not taken in consideration */
    private List<Attribute> session(Session session, String data){
        return List.of(new Attribute("something",data), new Attribute("id", session.getId()));
    }
}

//@UIEventPage(path = "", file = "/pages/sample")
class BadTestController {

    public List<Attribute> callable(String something, Session session ){
        return callable(session, something);
    }

    public List<Attribute> callable(Session session ){
        return callable(session, "");
    }

    private List<Attribute> callable(Session session, String data){
        return List.of(new Attribute("something",data), new Attribute("id", session.getId()));
    }
}