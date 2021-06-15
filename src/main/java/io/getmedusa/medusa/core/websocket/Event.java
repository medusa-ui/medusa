package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

    private String content;

    @JsonCreator
    public Event(@JsonProperty("content") String content) {
        this.content = content;
    }

    @JsonIgnore
    public String getContent() {
        return content;
    }


}