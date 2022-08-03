package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.MethodDetection;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
            final String methodName = socketAction.getAction().substring(0, socketAction.getAction().indexOf("("));
            final String clazz = bean.getClass().getName();


            EvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setVariable("session", session);

            String expression = escape(socketAction.getAction());
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
