package io.getmedusa.medusa.core.injector.tag.hydra;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LinkTagTest {

    private static final LinkTag TAG = new LinkTag();

    @Test
    void matchLinkWithStyleDoubleQuotes() {
        Pattern pattern = Pattern.compile(TAG.pattern(), Pattern.CASE_INSENSITIVE);
        String html = "<a id=\"hidden-link\" m-link=\"other-service/other-page\" m-link-style=\"hide\">This link will only show up if the hydra connection is active</a>";
        final Matcher matcher = pattern.matcher(html);
        String fullMatch = null;
        if (matcher.find()) {
            fullMatch = matcher.group(0);
        }
        Assertions.assertEquals("m-link=\"other-service/other-page\" m-link-style=\"hide\"", fullMatch);
    }

    @Test
    void matchLinkWithStyleSingleQuotes() {
        Pattern pattern = Pattern.compile(TAG.pattern(), Pattern.CASE_INSENSITIVE);
        String html = "<a id='hidden-link' m-link='other-service/other-page' m-link-style='hide'>This link will only show up if the hydra connection is active</a>";
        final Matcher matcher = pattern.matcher(html);
        String fullMatch = null;
        if (matcher.find()) {
            fullMatch = matcher.group(0);
        }
        Assertions.assertEquals("m-link='other-service/other-page' m-link-style='hide'", fullMatch);
    }

    @Test
    void swappedMatchLinkWithStyleDoubleQuotes() {
        Pattern pattern = Pattern.compile(TAG.pattern(), Pattern.CASE_INSENSITIVE);
        String html = "<a id=\"hidden-link\" m-link-style=\"hide\" m-link=\"other-service/other-page\">This link will only show up if the hydra connection is active</a>";
        final Matcher matcher = pattern.matcher(html);
        String fullMatch = null;
        if (matcher.find()) {
            fullMatch = matcher.group(0);
        }
        Assertions.assertEquals("m-link-style=\"hide\" m-link=\"other-service/other-page\"", fullMatch);
    }

    @Test
    void swappedMatchLinkWithStyleSingleQuotes() {
        Pattern pattern = Pattern.compile(TAG.pattern(), Pattern.CASE_INSENSITIVE);
        String html = "<a id='hidden-link' m-link-style='hide' m-link='other-service/other-page'>This link will only show up if the hydra connection is active</a>";
        final Matcher matcher = pattern.matcher(html);
        String fullMatch = null;
        if (matcher.find()) {
            fullMatch = matcher.group(0);
        }
        Assertions.assertEquals("m-link-style='hide' m-link='other-service/other-page'", fullMatch);
    }

    @Test
    void onlyLink() {
        Pattern pattern = Pattern.compile(TAG.pattern(), Pattern.CASE_INSENSITIVE);
        String html = "<a id='hidden-link' m-link='other-service/other-page'>This link will only show up if the hydra connection is active</a>";
        final Matcher matcher = pattern.matcher(html);
        String fullMatch = null;
        if (matcher.find()) {
            fullMatch = matcher.group(0);
        }
        Assertions.assertEquals("m-link='other-service/other-page'", fullMatch);
    }
}
