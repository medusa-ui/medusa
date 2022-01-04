package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

@UIEventPage(path = "/test/null-condition", file ="/pages/integration-tests/null-condition", setup = "pageAttributes")
public class NullConditionEventHandler {

    public PageAttributes pageAttributes(){
        return new PageAttributes().with("null-condition", null).with("title","Allow null-condition checks");
    }

    public DOMChanges setData() {
        return DOMChanges.of("null-condition","'some data...'");
    }

    public DOMChanges resetData() {
        return DOMChanges.of("null-condition",null);
    }
}
