package io.getmedusa.medusa.core.util;

import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;

public abstract class ExpressionEval {

    public static boolean eval(String condition) {
        boolean isVisible;
        try {
            IExpressionEvaluator ee = CompilerFactoryFactory.getDefaultCompilerFactory(ClassLoader.getSystemClassLoader()).newExpressionEvaluator();
            ee.setExpressionType(Boolean.class);
            ee.cook(condition);
            isVisible = (Boolean) ee.evaluate();
        } catch (Exception e) {
            throw new RuntimeException("Cannot eval '"+condition+"'", e);
        }
        return isVisible;
    }

}
