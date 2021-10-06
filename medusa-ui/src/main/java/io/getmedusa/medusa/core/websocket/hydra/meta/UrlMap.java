
package io.getmedusa.medusa.core.websocket.hydra.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "knownRoutes"
})
public class UrlMap {

    @JsonProperty("knownRoutes")
    private List<KnownRoute> knownRoutes = null;

    @JsonProperty("knownRoutes")
    public List<KnownRoute> getKnownRoutes() {
        return knownRoutes;
    }

}
