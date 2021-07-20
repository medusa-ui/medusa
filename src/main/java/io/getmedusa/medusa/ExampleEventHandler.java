package io.getmedusa.medusa;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChange;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExampleEventHandler implements UIEventController {
    private int increase = 0;
    private int counter = 0;
    private final List<String> listOfItemsBought = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>(Arrays.asList(new Order(new Product("Whitewood"),5),new Order(new Product("Darkwoods"),3)));
    private Product blueSky =  new Product("Blue Sky");
    private Map<String, Integer> counters = new HashMap<>();

    @Override
    public PageSetup setupPage() {
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
        return new PageSetup(
                "/",
                "hello-world",
                modelMap);
    }

    // bugfix: m-click-handling
    public List<DOMChange> increaseMyCounter(String uuid, int increase) {
        counters.put(uuid, counters.get(uuid) + increase);
        return Collections.singletonList(new DOMChange("my-counter", counters.get(uuid)));
    }

    public List<DOMChange> increaseCounter(Integer parameter) {
        counter += parameter;
        if(counter > 10) {
            counter = 0;
        }

        return Collections.singletonList(new DOMChange("counter-value", counter));
    }

    public List<DOMChange> waitSeconds(int secondsToWait) {
        try { Thread.sleep(secondsToWait * 1000L); } catch (Exception e) {}
        return Collections.singletonList(new DOMChange("done-waiting", true));
    }

    public List<DOMChange> order() {
        orders.add(new Order(blueSky, 1));
        return Arrays.asList(new DOMChange("orders", orders), new DOMChange("three-items", orders.size() == 3 ));
    }

    public List<DOMChange> cancelOrder(String orderId) {
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.id.equals(orderId)) iterator.remove();
        }
        return Arrays.asList(new DOMChange("orders", orders), new DOMChange("three-items", orders.size() == 3 ));
    }

    public List<DOMChange> buy(Object... parameters) {
        StringBuilder itemsBought = new StringBuilder();
        String appender = "";
        for(Object param : parameters) {
            itemsBought.append(appender);
            itemsBought.append(param.toString());
            appender = ", ";
        }
        listOfItemsBought.add(itemsBought.toString());
        return Arrays.asList(
                new DOMChange("items-bought", listOfItemsBought),
                new DOMChange("last_bought", itemsBought.toString()),
                new DOMChange("items-bought-size", listOfItemsBought.size()));
    }

    public List<DOMChange> search(String valueToSearch, int someValue, String type, String name) {
        return Collections.singletonList(new DOMChange("search-result", UUID.randomUUID().toString() + ":" + valueToSearch));
    }

    public List<DOMChange> clear() {
        listOfItemsBought.clear();
        return Arrays.asList(
                new DOMChange("items-bought", listOfItemsBought),
                new DOMChange("items-bought-size", 0));
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
