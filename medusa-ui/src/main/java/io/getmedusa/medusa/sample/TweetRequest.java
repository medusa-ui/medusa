package io.getmedusa.medusa.sample;

import java.util.Map;

public class TweetRequest {
    private String author;
    private Map<String, Object> metadata;

    public TweetRequest() {}

    public TweetRequest(String author, Map<String, Object> metadata) {
        this.author = author;
        this.metadata = metadata;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}