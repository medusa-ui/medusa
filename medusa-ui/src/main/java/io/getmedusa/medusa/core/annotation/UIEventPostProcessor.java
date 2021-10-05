package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.util.FilenameHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * This class triggers the post-processing. This is extensively relied upon to setup the registry at startup. <br/>
 * Note: This is post-processing <i>before</i> initialization of all beans. So it happens before any bean has started up.
 */
@Component
class UIEventPostProcessor implements BeanPostProcessor {

    private final boolean hydraEnabled;
    public UIEventPostProcessor(@Value("${hydra.enabled}") Boolean hydraEnabled) {
        if(hydraEnabled == null) hydraEnabled = false;
        this.hydraEnabled = hydraEnabled;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final UIEventPage uiEventPage = bean.getClass().getAnnotation(UIEventPage.class);
        if(uiEventPage != null) {
            final String path = uiEventPage.path();
            final String htmlFile = FilenameHandler.removeExtension(FilenameHandler.normalize(uiEventPage.file()));
            RouteRegistry.getInstance().add(path, htmlFile);

            if(hydraEnabled) {
                final HydraMenu menu = bean.getClass().getAnnotation(HydraMenu.class);
                if(null != menu) RouteRegistry.getInstance().addMenuItem(menu.value(), menu.label(), path);
            }

            EventHandlerRegistry.getInstance().add(path, bean);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
