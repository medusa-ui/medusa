package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.router.request.Route;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class RouteTest {

    @Test
    void testHashIsUnique() {
        Route routeA = new Route("/Ea", "", null);
        Route routeB = new Route("/FB", "", null);

        Assertions.assertEquals(routeA.getPath().hashCode(), routeB.getPath().hashCode(), "Two routes are hash collisions");
        Assertions.assertNotEquals(routeA.generateHash(), routeB.generateHash());
    }

    @Test
    void testFQDN() {
        Route route = new Route("/test", "", new EmbeddedSampleController());
        Assertions.assertEquals("io.getmedusa.medusa.core.router.request.RouteTest$EmbeddedSampleController", route.getControllerFQDN());
    }

    @Test
    void testGetTemplateHTML() {
        Route route = new Route("/test", "123", new EmbeddedSampleController());
        Assertions.assertEquals("123", route.getTemplateHTML());
    }

    @Test
    void testSetupAttributes() {
        Route route = new Route("/test", "", new EmbeddedSampleController());
        List<Attribute> attributes = route.getSetupAttributes(null);
        Assertions.assertNotNull(attributes);
        Assertions.assertEquals(1, attributes.size());
        Assertions.assertEquals(456, attributes.get(0).value());
    }

    @UIEventPage(path = "/", file = "/pages/hello-world")
    public class EmbeddedSampleController {

        public List<Attribute> setupAttributes() {
            return List.of(new Attribute("123", 456));
        }

    }

}
