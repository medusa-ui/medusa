package com.sample.medusa.eventhandler;

import com.sample.medusa.service.DemoService;
import io.getmedusa.medusa.core.annotation.DOMChanges;
import io.getmedusa.medusa.core.annotation.MEventPage;

@MEventPage(path = "/hello", file = "pages/hello")
public class HelloMEventPage {

    private final DemoService service;
    private String uuid;

    public HelloMEventPage(DemoService service) {
        this.service = service;
        this.uuid = service.uuid();
    }

    @DOMChanges("uuid")
    public void renew(){
        uuid = service.uuid();
    }

    public String getUuid() {
        return uuid;
    }
}
