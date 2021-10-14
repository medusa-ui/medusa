package io.getmedusa.medusa.core.websocket.hydra;

public record HydraMenuItem(String endpoint, String label) {

    public String labelWithFallback() {
        if(label == null || label.isBlank()) return endpoint();
        return label;
    }
}
