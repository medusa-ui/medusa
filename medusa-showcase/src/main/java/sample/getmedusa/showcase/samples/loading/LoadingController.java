package sample.getmedusa.showcase.samples.loading;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;

import java.util.List;

@UIEventPage(path = "/detail/sample/loading", file = "/pages/sample/loading.html")
public class LoadingController {

    public List<Attribute> loadingLogic() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            //ignore
        }
        return List.of(new Attribute(StandardAttributeKeys.LOADING, "loading-done"));
    }

}
