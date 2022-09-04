package sample.getmedusa.showcase.samples.embedding.fragment;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;

@UIEventPage(path = "/detail/sample/fragments", file = "/pages/sample/fragments.html")
public class FragmentController {

    public List<Attribute> setupAttributes(){
        return List.of(
                new Attribute("myRefName", "a-sample-ref"),
                new Attribute("nonExistentRef", "a-ref-that-does-not-exist"));
    }



}
