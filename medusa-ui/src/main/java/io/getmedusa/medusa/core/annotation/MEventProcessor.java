package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MEventProcessor {

    final ConfigurableListableBeanFactory beanFactory;
    final EventHandlerRegistry eventHandlerRegistry = EventHandlerRegistry.getInstance();
    final RouteRegistry routeRegistry = RouteRegistry.getInstance();

    public MEventProcessor(@Value("${medusa.scan:io.getmedussa}") String packages, ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        scanPackagesForMEventPages(Arrays.asList(packages.split(",")));
    }

    private void scanPackagesForMEventPages(List<String> packages) {
        try {
            ClassPathScanningCandidateComponentProvider uiEventPageProvider = new ClassPathScanningCandidateComponentProvider(false);
            uiEventPageProvider.addIncludeFilter(new AnnotationTypeFilter(UIEventPage.class));
            for (String pack : packages) {
                for (BeanDefinition beanDefinition : uiEventPageProvider.findCandidateComponents(pack)) {
                    String name = beanDefinition.getBeanClassName();
                    Class<?> beanClass = Class.forName(name);
                    for (Annotation annotation : beanClass.getAnnotations()) {
                        if (annotation instanceof MEventPage) {
                            MEventPage uiEventPage = (MEventPage) annotation;
                            String path = uiEventPage.path();
                            String page = uiEventPage.file();

                            eventHandlerRegistry.add(page, new MEventPageHandler(beanFactory,  beanClass));
                            routeRegistry.add(path,page);

                            System.out.println("MEventPage with path: " + path + ", page: " + page + ", class: " + beanClass.getSimpleName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
