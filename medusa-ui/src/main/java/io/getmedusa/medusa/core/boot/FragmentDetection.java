package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FragmentUtils;
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

    private final Map<String, List<String>> rootFragmentsUsed = new HashMap<>();

    public static String findPrefix(Document document) {
        try {
            final Element htmlTag = document.getElementsByTag("html").get(0);
            for (Attribute attribute : htmlTag.attributes()) {
                if (attribute.getValue().contains("getmedusa.io")) {
                    return attribute.getKey().split(":")[1];
                }
            }

            Elements refs = document.getElementsByAttribute("ref");
            if(!refs.isEmpty()) {
                Element ref = refs.get(0);
                if(ref.tagName().contains(":fragment")) {
                    return ref.tagName().replace(":fragment", "");
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    //this method detects fragments and registers them, within the rendering process we can then easily check for the presence of these fragments and replace them as needed
    String prepFile(final Object bean, final String html) {
        if(html.contains(":fragment>")) {
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
            Fragment fragment = findExistingRef(refElement).orElse(null);

            if(fragment == null) {
                fragment = new Fragment();
                fragment.setFallback(refElement.html());
                fragment.setService(orSelfAsDefault(refElement.attributes().get("service")));
                fragment.setRef(refElement.attributes().get("ref"));
                fragment.setId("$#FRGM-" + RandomUtils.generateId());

                detectedFragments.put(fragment.getId(), fragment);
                if(null != bean) {
                    final String controller = bean.getClass().getName();
                    List<String> refs = rootFragmentsUsed.getOrDefault(controller, new ArrayList<>());
                    refs.add(fragment.getRef());
                    rootFragmentsUsed.put(controller, refs);
                }
            }

            boolean isFragment = FragmentUtils.determineIfFragment(document);
            String outerHtml = document.outerHtml();
            if(isFragment) {
                outerHtml = document.body().html();
            }
            return prepFile(bean, outerHtml.replace(refElement.outerHtml(), fragment.getId()));
        }
        return html;
    }

    public Map<String, List<String>> getRootFragmentsUsed() {
        return rootFragmentsUsed;
    }

    private Optional<Fragment> findExistingRef(Element refElement) {
        return detectedFragments.values().stream()
                .filter(f -> f.getRef().equals(refElement.attributes().get("ref")))
                .findFirst();
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
        final List<Fragment> newFragments = new ArrayList<>();
        for(Fragment fragment : fragments) {
            String service = SpELUtils.parseExpression(fragment.getService(), session);
            if(service == null || service.isEmpty()) {
                service = "self";
            }
            final Fragment clonedFragment = fragment.clone();
            clonedFragment.setService(service);
            clonedFragment.setRef(SpELUtils.parseExpression(fragment.getRef(), session));
            newFragments.add(clonedFragment);
        }
        return newFragments;
    }

    public void clear() {
        detectedFragments.clear();
    }
}
