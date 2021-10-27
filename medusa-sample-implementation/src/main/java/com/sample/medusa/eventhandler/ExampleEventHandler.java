package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.*;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/", file = "/pages/hello-world")
public class ExampleEventHandler {
    private int increase = 0;
    private int counter = 0;
    private final List<String> listOfItemsBought = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>(Arrays.asList(new Order(new Product("Whitewood"),5),new Order(new Product("Darkwoods"),3)));
    private final Product blueSky =  new Product("Blue Sky");
    private final Map<String, Integer> counters = new HashMap<>();

    public PageAttributes setupAttributes(ServerRequest request){
        String uuid=Integer.toString(new Random().nextInt());
        counters.put(uuid, 0);

        return new PageAttributes()
                .with("uuid", uuid)
                .with("increase", ++increase)
                .with("counter-value", counter)
                .with("my-counter", counters.get(uuid))
                .with("last_bought", "메두사")
                .with("items-bought", listOfItemsBought)
                .with("items-bought-size", listOfItemsBought.size())
                .with("orders", orders)
                .with("blue-sky", blueSky.name)
                .with("three-items", orders.size() == 3)
                .with("search", "initial value!")
                .with("done-waiting", false)
                .with("search-result", "")

                //.with("principal", securityContext.getUserDetails().getUsername())

                // query param + conversion
                .with("query-param-q", request.queryParam("q").orElse("nothing"), parameterValue -> "query parameter q: " + parameterValue)
                // query param, no conversion
                .with("query-param-s", request.queryParam("s").orElse(""));
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
        orders.removeIf(order -> order.id.equals(orderId));
        return of("orders", orders).and("three-items", orders.size() == 3 );
    }

    public DOMChanges sampleWithSecurity(SecurityContext securityContext, WebSocketSession session) {
        System.out.println("test in session " + session.getId());
        return DOMChanges.empty();
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
