package io.getmedusa.medusa.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ReflectionUtil {

    /**
     * Find a getter-method for a property in the given class
     * The method name should start with get or is
     *
     * @param propertyName property name
     * @param clazz containing class
     * @return the corresponding method or null if not found
     */
    public static Method getter(String propertyName, Class<?> clazz) {
        Method method = null;
        List<String> methodNames = Arrays.stream(clazz.getDeclaredMethods()).map(Method::getName).collect(Collectors.toList());
        try {
            String namePart = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            String getterName = "get" + namePart;
            String isName = "is" + namePart;
            if(methodNames.contains(getterName)){
                method = clazz.getMethod(getterName);
            } else {
                method = clazz.getMethod(isName);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /**
     * Determine if a method is a getter-method
     * To be a getter-method the method name should start with 'get' or 'is'
     *
     * @param method the method
     * @return true if a getter-method
     */
    public static boolean isGetterMethod(Method method) {
        String name = method.getName();
        return name.startsWith("get") || name.startsWith("is");
    }

    /**
     * Get the property name of a getter Method.
     * The method name should start with get or is
     *
     * @param getter Method
     * @return the corresponding property or null when not a valid getter
     */
    public static String property(Method getter) {
        String name = null;
        String methodName = getter.getName();
        if(isGetterMethod(getter)) {
          name = methodName.replaceFirst("get|is","");
          name = name.substring(0,1).toLowerCase() + name.substring(1);
        }
        return name;
    }
}
