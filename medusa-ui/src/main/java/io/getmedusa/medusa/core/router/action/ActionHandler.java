package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.FormDetection;
import io.getmedusa.medusa.core.boot.MethodDetection;
import io.getmedusa.medusa.core.boot.RefDetection;
import io.getmedusa.medusa.core.router.action.converter.PojoTypeConverter;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ActionHandler {

    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    public Session executeAndMerge(SocketAction socketAction, Route route, Session session) {
        //find controller from cache
        Object bean = route.getController();

        //execute action
        final List<Attribute> attributes = execute(session, socketAction, bean);

        //merge attributes with attributes from session
        return session.merge(attributes);
    }

    private List<Attribute> execute(Session session, SocketAction socketAction, Object bean) {
        Object result = null;

        if(socketAction.getAction() != null) {
            if(null != socketAction.getFragment()) {
                final UIEventPageCallWrapper beanByRef = RefDetection.INSTANCE.findBeanByRef(socketAction.getFragment());
                if(null != beanByRef) {
                    bean = beanByRef.getController();
                }
            }

            final String methodName = socketAction.getAction().substring(0, socketAction.getAction().indexOf("("));
            final String clazz = bean.getClass().getName();

            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.addPropertyAccessor(new MapAccessor());

            Class<Object> formClass = FormDetection.INSTANCE.getFormClass(clazz, methodName);
            if(formClass != null) {
                evaluationContext.setTypeConverter(new PojoTypeConverter(formClass).getConverter());
            }

            evaluationContext.setVariable("session", session);

            String expression = escape(socketAction.getAction());
            expression = handleArrayParsing(expression);

            if(MethodDetection.INSTANCE.shouldBeginWithSession(clazz, methodName)) {
                expression = expression
                        .replace("(", "(#session,");
            } else if(MethodDetection.INSTANCE.shouldEndWithSession(clazz, methodName)) {
                expression = expression
                        .replace(")", ",#session)")
                        .replace("(,#session", "(#session");
            }

            result = SPEL_EXPRESSION_PARSER
                    .parseExpression(expression)
                    .getValue(evaluationContext, bean);
        }

        if(null == result) {
            return new ArrayList<>();
        }
        return (List<Attribute>) result;
    }

    private static String handleArrayParsing(String expression) {
        Pattern pattern = Pattern.compile("\\[.*?]");
        Matcher matcher = pattern.matcher(expression);
        while(matcher.find()) {
            final String part = matcher.group();
            expression = expression.replace(part, "{" + part.substring(1, part.length() -1) + "}");
        }
        return expression;
    }

    String escape(String raw) {
        if(raw == null) {
            return null;
        }
        try {
            return URLDecoder.decode(raw.replace("%27", "''"), Charset.defaultCharset().displayName());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
