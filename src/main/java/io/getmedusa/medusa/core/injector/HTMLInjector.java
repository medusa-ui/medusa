package io.getmedusa.medusa.core.injector;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HTMLInjector {

    INSTANCE;

    public String inject(Resource html) {
        try {
            String htmlString = StreamUtils.copyToString(html.getInputStream(), StandardCharsets.UTF_8);
            return htmlStringInject(htmlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String htmlStringInject(String htmlString) {
        final String tag = "m-click";
        Pattern pattern = Pattern.compile(tag + "=(\"|').*(\"|')", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlString);

        Map<String, String> replacements = new HashMap<>();
        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            String tagContent = fullMatch.replaceFirst(tag + "=", "");
            tagContent = tagContent.substring(1, tagContent.length() - 1);

            replacements.put(fullMatch, "onclick=\"sE('"+tagContent+"')\"");
        }

        for(Map.Entry<String, String> entrySet : replacements.entrySet()) {
            htmlString = htmlString.replace(entrySet.getKey(), entrySet.getValue());
        }

        //add script
        htmlString = htmlString.replaceFirst("</body>", "<script>function sE(e) { console.log(e); }</script>\n</body>");
        return htmlString;
    }
}
