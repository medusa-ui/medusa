
package io.getmedusa.medusa.core.websocket.hydra.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "service",
    "availableRoutes"
})
public class KnownRoute {

    @JsonProperty("service")
    private String service;
    @JsonProperty("availableRoutes")
    private List<String> availableRoutes = null;

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("availableRoutes")
    public List<String> getAvailableRoutes() {
        return availableRoutes;
    }
}
