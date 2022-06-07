package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.boot.RouteDetection;
import io.getmedusa.medusa.core.router.request.IRequestStreamHandler;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.router.request.RouteSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

class RouteSetupTest {

    private RouteSetup routeSetup;

    @Mock
    private IRequestStreamHandler requestStreamHandler;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(requestStreamHandler.startSessionAndBuildHTML(Mockito.any(Route.class))).thenReturn(request -> ServerResponse.ok().bodyValue("ok"));
        routeSetup = new RouteSetup();
    }

    @Test
    void testHtmlRouter() {
        RouteDetection.INSTANCE.consider(new EmbeddedSampleController());
        Assertions.assertThrows(IllegalStateException.class, () -> routeSetup.htmlRouter(null));

        final RouterFunction<ServerResponse> serverResponseRouterFunction = routeSetup.htmlRouter(requestStreamHandler);
        final String mapping = serverResponseRouterFunction.toString();
        Assertions.assertTrue(mapping.startsWith("(GET && /test-123) -> io.getmedusa.medusa.core.router.request.RouteSetupTest"), "Expect router to map to proper get call and internal controller");
    }

    @Test
    void testResourcesRouter() {
        final RouterFunction<ServerResponse> serverResponseRouterFunction = routeSetup.resourcesRouter();
        Assertions.assertEquals("/static/** -> class path resource [static/]", serverResponseRouterFunction.toString());
    }

    @UIEventPage(path = "/test-123", file = "/pages/sample")
    public class EmbeddedSampleController {

    }

}
