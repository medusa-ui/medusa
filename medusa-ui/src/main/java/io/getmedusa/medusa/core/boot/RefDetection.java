package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public enum RefDetection {

    INSTANCE;

    private final Map<String, String> detectedRefs = new HashMap<>();
    private final Map<String, UIEventPageCallWrapper> refToBeanMap = new HashMap<>();

    public void consider(Object bean) {
        final UIEventPage annotation = retrieveAnnotation(bean);
        if(null != annotation) {
            String fullTemplate = FileUtils.load(annotation.file());

            Map<String, String> refsInPage = findRefs(fullTemplate);

            //in addition to storing refs in the page and their html, we also need to remember the beans
            if(!refsInPage.isEmpty()) {
                for(String ref: refsInPage.keySet()) {
                    refToBeanMap.put(ref, new UIEventPageCallWrapper(bean));
                }
            }
            detectedRefs.putAll(refsInPage);
        }
    }

    Map<String, String> findRefs(String fullTemplate) {
        final HashMap<String, String> refMap = new HashMap<>();
        if(fullTemplate.contains(":ref=")) {
            final Document document = Jsoup.parse(fullTemplate);
            final String prefix = findPrefix(document);
            if(prefix == null) {
                return refMap;
            }
            final String refAttribute = prefix + ":ref";
            final Elements refElements = document.getElementsByAttribute(refAttribute);

            for(Element refElement : refElements) {
                String refName = refElement.attr(refAttribute);
                String refHTML = refElement.outerHtml();
                refMap.put(refName, refHTML);
            }
        }
        return refMap;
    }

    private String findPrefix(Document document) {
        try {
            final Element htmlTag = document.getElementsByTag("html").get(0);
            for (Attribute attribute : htmlTag.attributes()) {
                if (attribute.getValue().contains("getmedusa.io")) {
                    return attribute.getKey().split(":")[1];
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String findRef(String key) {
        return detectedRefs.getOrDefault(key, null);
    }

    public UIEventPageCallWrapper findBeanByRef(String key) { return refToBeanMap.getOrDefault(key, null); }

    private UIEventPage retrieveAnnotation(Object bean) {
        return bean.getClass().getAnnotation(UIEventPage.class);
    }

    public void updateAllRefsWithNestedFragments() {
        Map<String, String> updatesToRefs = new HashMap<>();
        for(Map.Entry<String, String> refEntry : detectedRefs.entrySet()) {
            final String html = refEntry.getValue();
            if(html.contains(":fragment>")) {
                updatesToRefs.put(refEntry.getKey(), FragmentDetection.INSTANCE.prepFile(null, html));
            }
        }
        detectedRefs.putAll(updatesToRefs);
    }

    public Map<String, UIEventPageCallWrapper> getRefToBeanMap() {
        return refToBeanMap;
    }
}
