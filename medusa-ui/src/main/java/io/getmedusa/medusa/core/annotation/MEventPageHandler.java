package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MEventPageHandler {
    private final ConfigurableListableBeanFactory beanFactory;
    private final Class clazz;
    private static Map<String, Object> sessionIDControllers = new ConcurrentHashMap<>();

    public MEventPageHandler(ConfigurableListableBeanFactory beanFactory, Class clazz) {
        this.beanFactory = beanFactory;
        this.clazz = clazz;
    }

    public static void addSession(WebSocketSession session) {
        if(!sessionIDControllers.containsKey(session.getId()))
            sessionIDControllers.put(session.getId(), null);
    }

    public Object bean(WebSocketSession session) {
        Object controller = sessionIDControllers.get(session.getId());
        if(controller == null) {
            controller = beanFactory.createBean(clazz, ConfigurableListableBeanFactory.AUTOWIRE_CONSTRUCTOR, true);
            sessionIDControllers.put(session.getId(), controller);
            Object sessionController = EventHandlerRegistry.getInstance().get(session);
        }
        return controller;
    }

    public static void removeSessionController(WebSocketSession session) {
        sessionIDControllers.remove(session.getId());
        System.out.println("removed session" + session.getId());
    }

    public Object instance() {
        return beanFactory.createBean(clazz, ConfigurableListableBeanFactory.AUTOWIRE_CONSTRUCTOR, true);
    }
}
