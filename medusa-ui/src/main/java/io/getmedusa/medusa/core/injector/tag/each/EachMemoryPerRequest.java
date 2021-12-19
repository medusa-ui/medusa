package io.getmedusa.medusa.core.injector.tag.each;

import java.util.HashMap;
import java.util.Map;

public class EachMemoryPerRequest {

    private final Map<String, EachMemoryPerEachName> eachMemoryPerEachNameMap = new HashMap<>();

    public void add(String eachName, Integer index, Object obj) {
        EachMemoryPerEachName existingMemory = eachMemoryPerEachNameMap.getOrDefault(eachName, new EachMemoryPerEachName());
        existingMemory.add(index, obj);
        eachMemoryPerEachNameMap.put(eachName, existingMemory);
    }

    public Object get(String eachName, Integer index) {
        EachMemoryPerEachName existingMemory = eachMemoryPerEachNameMap.get(eachName);
        if(null == existingMemory) return null;
        return existingMemory.get(index);
    }

}
