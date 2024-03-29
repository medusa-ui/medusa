package io.getmedusa.medusa.core.router.action;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.boot.FormDetection;
import io.getmedusa.medusa.core.boot.MethodDetection;
import io.getmedusa.medusa.core.boot.RefDetection;
import io.getmedusa.medusa.core.router.action.converter.PojoTypeConverter;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.validation.ValidationError;
import io.getmedusa.medusa.core.validation.ValidationExecutor;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ActionHandler {

    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    private final ValidationMessageResolver resolver;

    public ActionHandler(ValidationMessageResolver resolver) {
        this.resolver = resolver;
    }

    public Mono<Session> executeAndMerge(SocketAction socketAction, Route route, Session session) {
        //find controller from cache
        Object bean = route.getController();

        //execute action
        final Mono<List<Attribute>> attributes = execute(session, socketAction, bean);

        //merge attributes with attributes from session
        return attributes.map(session::merge);
    }

    private Mono<List<Attribute>> execute(Session session, SocketAction socketAction, Object bean) {
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

            Class formClass = FormDetection.INSTANCE.getFormClass(clazz, methodName);
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


            final Set<ValidationError> violations = ValidationExecutor.INSTANCE.validate(expression, bean);
            if(violations.isEmpty()) {
                result = SPEL_EXPRESSION_PARSER
                        .parseExpression(expression)
                        .getValue(evaluationContext, bean);
                //if response is not a Mono<List<Attributes>> but a List<Attributes>, just wrap it!
                if(result != null && !result.getClass().getName().contains("publisher.Mono")) {
                    result = Mono.just(result);
                }
            } else {
                List<ServerSideDiff> validatorViolation = new ArrayList<>();
                for(ValidationError violation : violations) {
                    validatorViolation.add(ServerSideDiff.buildValidation(violation.formContext() + "#" + violation.field(), resolver.resolveMessage(violation, session.getLocale())));
                }
                return Mono.just(List.of(new Attribute(StandardAttributeKeys.VALIDATION, validatorViolation)));
            }
        }

        if(null == result) {
            return Mono.just(new ArrayList<>());
        }
        try {
            return ((Mono<List<Attribute>>) result).map(a -> {
                a.stream().filter(b -> b.value() == null).forEach(c -> session.removeAttributeByName(c.name()));
                return a.stream().filter(b -> b.value() != null).toList();
            });
        } catch (ClassCastException e) {
            return Mono.just(new ArrayList<>());
        }
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