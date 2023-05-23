package sample.getmedusa.showcase.samples.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.web.reactive.function.server.ServerRequest;
import sample.getmedusa.showcase.core.Versions;
import sample.getmedusa.showcase.core.controller.AbstractController;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/detail/query", file = "/pages/sample/query-param.html")
public class QueryParamController extends AbstractController {

    public List<Attribute> setupAttributes(ServerRequest request) {
        return $$(
                "sessionVar1", request.queryParam("sample1").orElse("No 'sample1' param provided?"),
                "sessionVar2", request.queryParam("sample2").orElse("No 'sample2' param provided?"),
                "version", Versions.getVersionFooter(),
                "serverCodeOnPageLoad", loadCode("/samples/controller/query/server-page-load.txt")[0],
                "clientOnAction", loadCode("/samples/controller/query/client-on-action.txt")[0],
                "serverCodeOnAction", loadCode("/samples/controller/query/server-on-action.txt")[0]
        );
    }

    public List<Attribute> runAction(Session session) {
        return $$("actionOutput",
                session.getAttribute("sessionVar1")
                + "/" +
                session.getAttribute("sessionVar2"));
    }

}

