package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class JSONUtilsTest {

    @Test
    void testSerializationLoop() {
        Map<String, Object> example = Map.of("X", 123, "Y", "ZYW");
        Map<String, Object> parsed = JSONUtils.deserialize(JSONUtils.serialize(example), Map.class);
        Assertions.assertNotNull(parsed);
        System.out.println(parsed);
    }

    @Test
    void testSerializationList() {
        Fragment fragment = new Fragment();
        fragment.setRef("xyz");
        fragment.setService("test");
        List<Fragment> fragments = List.of(fragment, fragment);
        String json = JSONUtils.serialize(fragments);
        System.out.println(json);
        List<Fragment> fragmentsDeserialized = JSONUtils.deserializeList(json, Fragment.class);
        Assertions.assertNotNull(fragmentsDeserialized);
        Assertions.assertEquals(fragments.size(), fragmentsDeserialized.size());
        System.out.println(fragmentsDeserialized);
    }

    @Test
    void testListOfList() {
        String toParse = "[[{\"renderedHTML\":\"<html>\\n <head></head>\\n <body>\\n  <div m:ref=\\\"search-bar\\\">\\n    This piece of code can be used in another app. \\n   <p>Search bar</p> End of code. \\n  </div>\\n </body>\\n</html>\",\"id\":\"$#FRGM-1206167147962400813603e0ff2e4a5a96063d6b8447cd0b29860\"}]]";

        String toParseObj = "{\"renderedHTML\":\"<html>\\n <head></head>\\n <body>\\n  <div m:ref=\\\"search-bar\\\">\\n    This piece of code can be used in another app. \\n   <p>Search bar</p> End of code. \\n  </div>\\n </body>\\n</html>\",\"id\":\"$#FRGM-1206167147962400813603e0ff2e4a5a96063d6b8447cd0b29860\"}";
        RenderedFragment fragment = JSONUtils.deserialize(toParseObj, RenderedFragment.class);

        String toParseList = "[{\"renderedHTML\":\"<html>\\n <head></head>\\n <body>\\n  <div m:ref=\\\"search-bar\\\">\\n    This piece of code can be used in another app. \\n   <p>Search bar</p> End of code. \\n  </div>\\n </body>\\n</html>\",\"id\":\"$#FRGM-1206167147962400813603e0ff2e4a5a96063d6b8447cd0b29860\"}]";
        List<RenderedFragment> fragments = JSONUtils.deserializeList(toParseList, RenderedFragment.class);
        System.out.println(fragments);
    }

}
