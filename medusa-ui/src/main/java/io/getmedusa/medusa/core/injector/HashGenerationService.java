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

    public void recursivelyAddPath(Document document, boolean applyHash) {
        recursivelyAddPath(document.children(), "", applyHash);
    }

    private void recursivelyAddPath(Elements elements, String context, boolean applyHash) {
        for(Element element : elements) {
            StringBuilder hashBasis = new StringBuilder(context);
            if(!context.isBlank()) {
                hashBasis.append(">");
            }
            hashBasis.append(element.tagName());

            List<Element> siblings = findSiblings(element);

            if(siblings.size() > 1) {
                hashBasis.append("[");
                hashBasis.append(siblings.indexOf(element));
                hashBasis.append("]");
            }

            final String newContext = hashBasis.toString();

            applyPathHash(applyHash, element, newContext);
            applyContentHash(applyHash, element);

            if(element.childrenSize() > 0) {
                recursivelyAddPath(element.children(), newContext, applyHash);
            }
        }
    }

    private void applyPathHash(boolean applyHash, Element element, String newContext) {
        element.attr("p", applyHash ? hash(newContext) : newContext);
    }

    private void applyContentHash(boolean applyHash, final Element element) {
        if(applyHash) {
            String directContent = element.ownText();
            if(!directContent.isBlank()) {

                if(element.childrenSize() > 0) {
                    Element clone = element.clone();
                    clone.children().remove();
                    directContent = clone.ownText();
                }

                element.attr("c", hash(directContent));
            }
        }
    }

    private List<Element> findSiblings(Element element) {
        if(element.parent() == null) return new ArrayList<>();
        return element.parent().children().stream().filter(s -> s.tagName().equals(element.tagName())).toList();
    }

    public String hash(String raw) {
        return HASH_FUNCTION.hashString(raw, StandardCharsets.UTF_8).toString();
    }

}
