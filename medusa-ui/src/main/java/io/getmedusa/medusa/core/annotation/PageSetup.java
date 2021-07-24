package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.util.FilenameHandler;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

public class PageSetup {

    private final String getPath;
    private final String htmlFile;
    private final Map<String, Object> pageVariables = new HashMap<>();
    private final Map<String, Converter> converters = new HashMap<>();

    @FunctionalInterface
    public interface Converter {
        static Converter stringToString = in -> in;
        Object convert(String in);
    }

    public PageSetup(String getPath, String htmlFile) {
        this(getPath, htmlFile, new HashMap<>());
    }

    public PageSetup(String getPath, String htmlFile, Map<String, Object> pageVariables) {
        this.getPath = getPath;
        this.htmlFile = htmlFile;
        if(null != pageVariables) this.pageVariables.putAll(pageVariables);
    }

    public PageSetup with(String key, Object value) {
        pageVariables.put(key, value);
        return this;
    }

    public String getGetPath() {
        return getPath;
    }

    public String getHtmlFile() {
        return FilenameHandler.removeExtension(FilenameHandler.normalize(htmlFile));
    }

    public Map<String, Object> getPageVariables() {
        return pageVariables;
    }

    public PageSetup withPathVariable(String key) {
        converters.put(key, Converter.stringToString);
        return this;
    }

    public PageSetup withPathVariable(String key, Converter converter) {
        converters.put(key, converter);
        return this;
    }
    
    public void resolve(String key, String value){
        Object converted = converters.get(key).convert(value);
        pageVariables.put(key, converted);
        System.out.println("PageSetup.resolve: " + key + " => " + value + " :: " + converted + " pageVariables.get(" + key + ") = " + pageVariables.get(key));
    }

    public void resolveMap(Map<String, String> pathVariables) {
        pathVariables.forEach(this::resolve);
        System.out.println("PageSetup.resolveMap: pageVariables: "  + pageVariables);
    }

    public void resolveMultiValueMap(MultiValueMap<String, String> queryParams) {
        // TODO
    }

}