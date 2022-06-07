package io.getmedusa.medusa.core.util;

public class FilenameUtils {

    protected static final String HTML_EXTENSION = ".html";

    private FilenameUtils() {}

    public static String removeHTMLExtension(String fileNameWithExtension) {
        if(fileNameWithExtension == null) return null;
        if(fileNameWithExtension.endsWith(HTML_EXTENSION)) {
            return fileNameWithExtension.substring(0, fileNameWithExtension.length()-5);
        }
        return fileNameWithExtension;
    }

    public static String normalize(String pathToHTMLFile) {
        if(pathToHTMLFile == null || pathToHTMLFile.isBlank()) { throw new IllegalArgumentException("Path to file is not valid"); }
        String resourcePath = pathToHTMLFile;
        if(!resourcePath.endsWith(HTML_EXTENSION)) resourcePath = resourcePath + HTML_EXTENSION;
        if(resourcePath.startsWith("/")) resourcePath = resourcePath.substring(1);
        return resourcePath;
    }

}
