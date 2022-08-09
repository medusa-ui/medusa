package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.RandomUtils;
import io.getmedusa.medusa.core.util.SpELUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public enum FragmentDetection {

    INSTANCE;

    private final Map<String, Fragment> detectedFragments = new HashMap<>();

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

    //this method detects fragments and registers them, within the rendering process we can then easily check for the presence of these fragments and replace them as needed
    String prepFile(final String html) {
        if(html.contains(":fragment ")) {
            final Document document = Jsoup.parse(html);
            document.outputSettings().indentAmount(0).prettyPrint(false);
            final String prefix = findPrefix(document);
            if(prefix == null) {
                return html;
            }

            final String refAttribute = prefix + ":fragment";
            final Elements refElements = document.getElementsByTag(refAttribute);
            if(refElements.isEmpty()) {
                return html;
            }
            Element refElement = refElements.get(0);
            Fragment fragment = new Fragment();
            fragment.setFallback(refElement.html());
            fragment.setService(orSelfAsDefault(refElement.attributes().get("service")));
            fragment.setRef(refElement.attributes().get("ref"));
            fragment.setId("$#FRGM-" + RandomUtils.generateId());

            detectedFragments.put(fragment.getId(), fragment);

            return prepFile(document.outerHtml().replace(refElement.outerHtml(), fragment.getId()));
        }
        return html;
    }

    private String orSelfAsDefault(String service) {
        if(null == service || service.isEmpty()) {
            return "self";
        }
        return service;
    }

    public Set<String> getFragmentIds() {
        return detectedFragments.keySet();
    }

    public Map<String, List<Fragment>> detectWhichFragmentsArePresent(String templateHTML, Session session) {
        final Map<String, List<Fragment>> result = new HashMap<>();

        for(Map.Entry<String, Fragment> fragmentEntry : detectedFragments.entrySet()) {
            if (templateHTML.contains(fragmentEntry.getKey())) {
                String service = SpELUtils.parseExpression(fragmentEntry.getValue().getService(), session);
                final List<Fragment> fragments = result.getOrDefault(service, new ArrayList<>());
                fragments.add(fragmentEntry.getValue());
                result.put(service, resolveFragments(fragments, session));
            }
        }

        return result;
    }

    private List<Fragment> resolveFragments(List<Fragment> fragments, Session session) {
        for(Fragment fragment : fragments) {
            String service = SpELUtils.parseExpression(fragment.getService(), session);
            if(service == null || service.isEmpty()) {
                service = "self";
            }
            fragment.setService(service);
            fragment.setResolvedRef(SpELUtils.parseExpression(fragment.getRef(), session));
        }
        return fragments;
    }

    public void clear() {
        detectedFragments.clear();
    }
}
