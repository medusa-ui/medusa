package io.getmedusa.medusa.core.router.action.converter;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.support.StandardTypeConverter;

import java.util.Map;

public class PojoTypeConverter {

    private final TypeConverter typeConverter;

    public PojoTypeConverter(Class<Object> clazz) {
        GenericConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(Map.class, clazz, new PojoConverter(clazz));
        typeConverter = new StandardTypeConverter(conversionService);
    }

    public TypeConverter getConverter() {
        return typeConverter;
    }
}
