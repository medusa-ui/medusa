package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.expression.spel.standard.SpelExpressionParser;
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
        final List<Attribute> attributes = execute(socketAction, bean);

        //merge attributes with attributes from session
        return session.merge(attributes);
    }

    private List<Attribute> execute(SocketAction socketAction, Object bean) {
        Object result = SPEL_EXPRESSION_PARSER.parseExpression(escape(socketAction.getAction())).getValue(bean);
        if(null == result) return new ArrayList<>();
        return (List<Attribute>) result;
    }

    String escape(String raw) {
        if(raw == null) return null;
        try {
            return URLDecoder.decode(raw.replace("%27", "''"), Charset.defaultCharset().displayName());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
