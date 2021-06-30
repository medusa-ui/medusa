package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.util.ExpressionEval;
import io.getmedusa.medusa.core.util.SpelExpressionParserHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ConditionalRegistry.class);
    private static final ConditionalRegistry INSTANCE = new ConditionalRegistry();
    private static final String PREFIX = "$";

    private ConditionalRegistry() { }

    public static ConditionalRegistry getInstance() {
        return INSTANCE;
    }
    private final Map<String, String> registry = new HashMap<>();

    public void add(String divId, String condition){
        registry.put(divId, condition);
    }
    public String get(String divId) { return registry.get(divId); }

    public boolean evaluate(String divId, Map<String, Object> variables) {
        String condition = registry.get(divId);
        if(null == condition) return false;
        return ExpressionEval.eval(parseCondition(condition, variables));
    }

    private String parseCondition(String conditionParsed, Map<String, Object> variables) {
        Pattern pattern = Pattern.compile("\\$(\\S*)");
        Matcher matcher = pattern.matcher(conditionParsed);
        logger.debug("TO PARSE: {} match count: {}", conditionParsed, matcher.groupCount());
        while (matcher.find()) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String condition = matcher.group().replace(PREFIX, "");
                logger.debug("CONDITION: {}" , condition);
                Object value = entry.getValue();
                String key = entry.getKey();
                if (conditionParsed.contains(PREFIX) && condition.startsWith("" + entry.getKey()) &&  condition.contains(".")) {
                    String expression = condition.substring(condition.lastIndexOf(".") + 1);
                    // TODO find a way to detect the expression correctly
                    while (expression.replaceAll("\\)","").length() != expression.replaceAll("\\(","").length()) {
                        expression = expression.replaceFirst("\\)","");
                        condition = condition.replaceFirst("\\)", "");
                    }
                    logger.debug( "condition: {} => expression: {}", condition, expression);
                    String replace = SpelExpressionParserHelper.getStringValue(expression, value);
                    logger.debug("SPeL replacement: '{}' with '{}' in {}" , PREFIX + condition, replace, conditionParsed);
                    conditionParsed = conditionParsed.replace(PREFIX + condition, replace);
                } else if (conditionParsed.contains(PREFIX) && condition.startsWith("" + entry.getKey()) ){
                    logger.debug("value replacement: '{}' with '{}' in {}" , PREFIX + condition, value.toString(), conditionParsed);
                    conditionParsed = conditionParsed.replace(PREFIX + key, value.toString());
                }
                if (! conditionParsed.contains(PREFIX)) break;
            }
        }

        /*
        for(Map.Entry<String, Object> variableEntrySet : variables.entrySet()) {
            String key = variableEntrySet.getKey();
            Object value = variableEntrySet.getValue();
            conditionParsed = conditionParsed.replace(PREFIX + key, value.toString());
        }
        */
        logger.debug("final conditionParsed: {}",conditionParsed);
        return conditionParsed;
    }

    public List<String> findByConditionField(String fieldWithChange) {
        final String prefixedField = PREFIX + fieldWithChange;
        final List<String> ids = new ArrayList<>();

        for (Map.Entry<String, String> entry : registry.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.contains(prefixedField)) {
                ids.add(key);
            }
        }
        return ids;
    }
}
