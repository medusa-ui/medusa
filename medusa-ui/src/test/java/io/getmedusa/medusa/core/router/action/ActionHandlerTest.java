package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.RouteDetection;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ActionHandlerTest {

    private ActionHandler actionHandler;

    @BeforeEach
    public void setup() {
        actionHandler = new ActionHandler(new ValidationMessageResolver(null));
    }

    @Test
    void testExecuteAndMerge_doActionWithoutParams() {
        SocketAction socketAction = new SocketAction();
        socketAction.setAction("doActionWithoutParams()");

        final Session updatedSession = actionHandler.executeAndMerge(socketAction, getRoute(), new Session()).block();
        System.out.println(updatedSession.toLastParameterMap());
        Assertions.assertEquals(updatedSession.toLastParameterMap().get("counterValue"), 1);
    }

    @Test
    void testExecuteAndMerge_doActionWithParam() {
        SocketAction socketAction = new SocketAction();
        socketAction.setAction("doActionWithParam('zyw-123-3213', 5678)");

        final Session updatedSession = actionHandler.executeAndMerge(socketAction, getRoute(), new Session()).block();
        System.out.println(updatedSession.toLastParameterMap());
        Assertions.assertEquals(updatedSession.toLastParameterMap().get("counterValue"), 5678);
        Assertions.assertEquals(updatedSession.toLastParameterMap().get("zyw-123-3213"), "123");
    }

    @Test
    void testEscape() {
        Assertions.assertNull(actionHandler.escape(null));
        Assertions.assertEquals("123", actionHandler.escape("123"));
        Assertions.assertEquals("안녕하세요 세계", actionHandler.escape("안녕하세요 세계"));
        Assertions.assertEquals("xYz123(1, 'bla')", actionHandler.escape("xYz123(1, 'bla')"));
    }

    private Route getRoute() {
        RouteDetection.INSTANCE.consider(new SampleController());
        return RouteDetection.INSTANCE.getDetectedRoutes().stream().findFirst().get();
    }

    @UIEventPage(path = "/", file = "/pages/hello-world")
    public class SampleController {

        public List<Attribute> doActionWithoutParams() {
            return List.of(new Attribute("counterValue", 1));
        }

        public List<Attribute> doActionWithParam(String param, Integer value) {
            return List.of(new Attribute(param, "123"), new Attribute("counterValue", value));
        }

    }

}
