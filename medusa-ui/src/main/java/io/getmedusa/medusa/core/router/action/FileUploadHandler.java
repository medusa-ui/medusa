package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FileUploadHandler {

    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    public static boolean isUploadRelated(SocketAction action) {
        return action.getFileMeta() != null;
    }

    Map<String, FileUploadWrapper> map = new HashMap<>();

    private void uploadComplete(FileUploadMeta fileMeta, Route route, Session session) {
        session.getPendingFileUploads().remove(fileMeta.getFileId());
        if(session.getPendingFileUploads().isEmpty()) {
            session.setPendingFileUploads(null);
        }

        //find controller from cache
        Object bean = route.getController();

        //execute action
        final String clazz = bean.getClass().getName();

        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.addPropertyAccessor(new MapAccessor());

        evaluationContext.setVariable("session", session);
        evaluationContext.setVariable("byteBuffer", map.get(fileMeta.getFileId()).getByteBuffer());

        System.out.println("Ready to call: " + fileMeta.getMethod() + " on " + clazz);

        SPEL_EXPRESSION_PARSER
                .parseExpression(fileMeta.getMethod())
                .getValue(evaluationContext, bean);

        //merge attributes with attributes from session
        //session.merge(attributes);
    }
}
