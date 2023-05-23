package sample.getmedusa.showcase.samples.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.web.reactive.function.server.ServerRequest;
import sample.getmedusa.showcase.core.Versions;
import sample.getmedusa.showcase.core.controller.AbstractController;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/detail/path/{sample1}/{sample2}", file = "/pages/sample/path-variable.html")
public class PathVariableController extends AbstractController {

    public List<Attribute> setupAttributes(ServerRequest request) {
        return $$(
                "sessionVar1", request.pathVariable("sample1"),
                "sessionVar2", request.pathVariable("sample2"),
                "version", Versions.getVersionFooter(),
                "serverCodeOnPageLoad", loadCode("/samples/controller/path/server-page-load.txt")[0],
                "clientOnAction", loadCode("/samples/controller/path/client-on-action.txt")[0],
                "serverCodeOnAction", loadCode("/samples/controller/path/server-on-action.txt")[0]
        );
    }

    public List<Attribute> runAction(Session session) {
        return $$("actionOutput",
                session.getAttribute("sessionVar1")
                + "/" +
                session.getAttribute("sessionVar2"));
    }

}

