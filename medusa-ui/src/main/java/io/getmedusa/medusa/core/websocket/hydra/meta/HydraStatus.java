
package io.getmedusa.medusa.core.websocket.hydra.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.getmedusa.medusa.core.websocket.hydra.HydraMenuItem;

import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "urlMap",
    "menuItems"
})
public class HydraStatus {

    @JsonProperty("urlMap")
    private UrlMap urlMap;
    @JsonProperty("menuItems")
    private Map<String, Set<HydraMenuItem>> menuItems;

    public HydraStatus() {
    }

    public HydraStatus(Map<String, Set<HydraMenuItem>> menuItems) {
        this.menuItems = menuItems;
    }

    @JsonProperty("urlMap")
    public UrlMap getUrlMap() {
        return urlMap;
    }

    @JsonProperty("menuItems")
    public Map<String, Set<HydraMenuItem>> getMenuItems() {
        return menuItems;
    }

}
