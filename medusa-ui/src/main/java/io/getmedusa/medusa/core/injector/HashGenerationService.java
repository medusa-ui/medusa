package io.getmedusa.medusa.core.injector;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HashGenerationService {

    private static final HashFunction HASH_FUNCTION = Hashing.murmur3_32_fixed();

    public void recursivelyAddPath(Document document) {
        recursivelyAddPath(document.children(), null, "");
    }

    private void recursivelyAddPath(Elements elements, Element previousElement, String pathSoFar) {
        for(Element element : elements) {
            pathSoFar = calculatePath(element, pathSoFar);
            applyHash(element, previousElement, pathSoFar);
            previousElement = element;

            if(element.childrenSize() > 0) {
                recursivelyAddPath(element.children(), previousElement, pathSoFar);
            }
        }
    }

    private String calculatePath(Element element, String pathSoFar) {
        StringBuilder hashBasis = new StringBuilder(pathSoFar);
        if(!pathSoFar.isBlank()) {
            hashBasis.append(">");
        }
        hashBasis.append(element.tagName());

        List<Element> siblings = findSiblings(element);

        if(siblings.size() > 1) {
            hashBasis.append("[");
            hashBasis.append(siblings.indexOf(element));
            hashBasis.append("]");
        }

        return hashBasis.toString();
    }

    private List<Element> findSiblings(Element element) {
        if(element.parent() == null) return new ArrayList<>();
        return element.parent().children().stream().filter(s -> s.tagName().equals(element.tagName())).toList();
    }


    private void applyHash(Element element, Element previousElement, String pathSoFar) {
        element.attr("b", (previousElement != null) ? previousElement.attr("c") : hash("%no-previous"));
        element.attr("c", getContentHash(element, pathSoFar));
    }

    private String getContentHash(final Element element, String pathSoFar) {
        String directContent = element.ownText();
        if(!directContent.isBlank()) {
            if(element.childrenSize() > 0) {
                Element clone = element.clone();
                clone.children().remove();
                directContent = clone.ownText();
            }
            return hash(pathSoFar + directContent);
        } else {
            return hash(pathSoFar + "%no-content");
        }
    }

    public String hash(String raw) {
        return HASH_FUNCTION.hashString(raw, StandardCharsets.UTF_8).toString();
    }

}
