package io.getmedusa.medusa.core.util;

public class FilenameHandler {

    private FilenameHandler() {}

    public static String removeExtension(String fullString) {
        if(fullString == null) return null;
        if(fullString.endsWith(".html")) {
            return fullString.substring(0, fullString.length()-5);
        }
        return fullString;
    }

    public static String normalize(String htmlFile) {
        String resourcePath = htmlFile;
        if(!resourcePath.endsWith(".html")) resourcePath = resourcePath + ".html";
        if(resourcePath.startsWith("/")) resourcePath = resourcePath.substring(1);
        return resourcePath;
    }

}
