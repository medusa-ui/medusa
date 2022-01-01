package io.getmedusa.medusa.core.injector.tag.each;

import java.util.HashMap;
import java.util.Map;

public class EachMemoryPerEachName {

    private final Map<Integer, Object> eachMemoryPerIndex = new HashMap<>();

    public void add(Integer index, Object obj) {
        eachMemoryPerIndex.put(index, obj);
    }

    public Object get(int index) {
        return eachMemoryPerIndex.get(index);
    }


}
