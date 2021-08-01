package com.sample.medusa.eventhandler;

import com.sample.medusa.service.DemoService;
import io.getmedusa.medusa.core.annotation.DomChanges;
import io.getmedusa.medusa.core.annotation.MEventPage;

import java.util.ArrayList;
import java.util.List;

@MEventPage(path = "/hello", file = "pages/hello")
public class HelloMEventPage {

    private final DemoService service;
    private String uuid;
    private List<String> history = new ArrayList<>();
    private int counter;

    public HelloMEventPage(DemoService service) {
        this.service = service;
    }

    public void init(){
        this.counter = service.getRandomNumber();
        generateUUIDAndStoreInHistory();
    }

    private void generateUUIDAndStoreInHistory(){
        String generated = service.uuid();
        this.uuid = generated;
        history.add(this.uuid);
    }

    @DomChanges({"uuid","history"})
    public void renew() {
        generateUUIDAndStoreInHistory();
    }

    @DomChanges({"counter"})
    public void increment() {
        counter++;
    }

    @DomChanges({"counter"})
    public void decrement() {
        counter--;
    }

    public int getCounter() {
        return counter;
    }

    public String getUuid() {
        return uuid;
    }

    public List<String> getHistory() {
        return history;
    }
}
