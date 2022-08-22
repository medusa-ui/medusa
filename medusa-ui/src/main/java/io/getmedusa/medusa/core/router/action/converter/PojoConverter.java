package io.getmedusa.medusa.core.router.action.converter;

import io.getmedusa.medusa.core.util.JSONUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public class PojoConverter implements Converter<Map, Object> {

    private Class<?> clazz;

    public PojoConverter(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object convert(Map source) {
        if(clazz.isAssignableFrom(Map.class)) {
            return source;
        }
        return JSONUtils.deserialize(JSONUtils.serialize(source), clazz);
    }

}
