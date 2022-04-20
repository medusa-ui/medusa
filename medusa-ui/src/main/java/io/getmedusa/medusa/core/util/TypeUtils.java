package io.getmedusa.medusa.core.util;

public class TypeUtils {

    public static boolean isNumeric(String s) {
        if (s == null) {
            return false;
        }
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isBoolean(String s) {
        if (s == null) {
            return false;
        }
        return "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
    }

}
