package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/", file = "/pages/hello-world")
public class ExampleEventHandler implements UIEventController {
    private int increase = 0;
    private int counter = 0;
    private final List<String> listOfItemsBought = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>(Arrays.asList(new Order(new Product("Whitewood"),5),new Order(new Product("Darkwoods"),3)));
    private Product blueSky =  new Product("Blue Sky");
    private Map<String, Integer> counters = new HashMap<>();

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext){
        String uuid= UUID.randomUUID().toString();
        counters.put(uuid, 0);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("uuid", uuid );
        modelMap.put("increase", ++increase );
        modelMap.put("counter-value", counter);
        modelMap.put("my-counter", counters.get(uuid));
        modelMap.put("last_bought", "Nothing yet!");
        modelMap.put("items-bought", listOfItemsBought);
        modelMap.put("items-bought-size", listOfItemsBought.size());
        modelMap.put("orders", orders);
        modelMap.put("blue-sky", blueSky.name);
        modelMap.put("three-items", orders.size() == 3 );
        modelMap.put("search", "initial value!");
        modelMap.put("done-waiting", false);
        modelMap.put("search-result", "");

        modelMap.put("principal", ((UserDetails) securityContext.getAuthentication().getPrincipal()).getUsername());
        return new PageAttributes(modelMap);
    }

    public DOMChanges increaseMyCounter(String uuid, int increase) {
        counters.put(uuid, counters.get(uuid) + increase);
        return of("my-counter", counters.get(uuid));
    }

    public DOMChanges increaseCounter(Integer parameter) {
        counter += parameter;
        if(counter > 10) {
            counter = 0;
        }

        return of("counter-value", counter);
    }

    public DOMChanges waitSeconds(int secondsToWait) {
        try { Thread.sleep(secondsToWait * 1000L); } catch (Exception e) {}
        return of("done-waiting", true);
    }

    public DOMChanges order() {
        orders.add(new Order(blueSky, 1));
        return of("orders", orders).and("three-items", orders.size() == 3 );
    }

    public DOMChanges cancelOrder(String orderId) {
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.id.equals(orderId)) iterator.remove();
        }
        return of("orders", orders).and("three-items", orders.size() == 3 );
    }

    public DOMChanges buy(Object... parameters) {
        StringBuilder itemsBought = new StringBuilder();
        String appender = "";
        for(Object param : parameters) {
            itemsBought.append(appender);
            itemsBought.append(param.toString());
            appender = ", ";
        }
        listOfItemsBought.add(itemsBought.toString());
        return of("items-bought", listOfItemsBought)
                .and("last_bought", itemsBought.toString())
                .and("items-bought-size", listOfItemsBought.size());
    }

    public DOMChanges search(String valueToSearch, int someValue, String type, String name) {
        return of("search-result", UUID.randomUUID() + ":" + valueToSearch);
    }

    public DOMChanges clear() {
        listOfItemsBought.clear();
        return of("items-bought", listOfItemsBought)
                .and("items-bought-size", 0);
    }
}

class Product {
    String name;

    public Product(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Order {
    String id;
    Product product;
    Integer number;

    public Order(Product product, Integer number) {
        this.id = UUID.randomUUID().toString();
        this.product = product;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public Product getProduct() {
        return product;
    }

}
