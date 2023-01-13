package io.getmedusa.medusa.core.boot.hydra.model;

import io.getmedusa.medusa.core.boot.hydra.model.meta.ActiveService;

import java.util.Set;

public class ActiveServiceOverview {

    private String serviceName;
    private Set<ActiveService> activeServices;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Set<ActiveService> getActiveServices() {
        return activeServices;
    }

    public void setActiveServices(Set<ActiveService> activeServices) {
        this.activeServices = activeServices;
    }
}
