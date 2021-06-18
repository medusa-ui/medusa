package io.getmedusa.medusa.core.util;

public class FilenameHandler {

    public static String removeExtension(String fullString) {
        if(fullString == null) return null;
        if(fullString.endsWith(".html")) {
            return fullString.substring(0, fullString.length()-5);
        }
        return fullString;
    }

}
