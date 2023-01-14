package io.getmedusa.medusa.core.boot.hydra.model;

import java.util.List;

public class RegistrationResponse {

    private String publicKey;
    private List<ActiveServiceOverview> services;

    public static RegistrationResponse error() {
        return new RegistrationResponse();
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public List<ActiveServiceOverview> getServices() {
        return services;
    }

    public void setServices(List<ActiveServiceOverview> services) {
        this.services = services;
    }
}
