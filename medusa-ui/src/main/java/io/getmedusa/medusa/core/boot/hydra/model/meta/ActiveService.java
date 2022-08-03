package io.getmedusa.medusa.core.boot.hydra.model.meta;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActiveService {

    // TODO: will be overridden when ServiceController get initialised
    public String webProtocol = "https";

    private String host;
    private int port;
    private int socketPort;
    private String name;
    private long version;
    private final long activeSince;
    private AwakeningType awakening;
    private Set<String> endpoints = new HashSet<>();
    private Set<String> sockets = new HashSet<>();
    private Set<String> staticResources = new HashSet<>();
    private Map<String, Set<MenuItem>> menuItems = new HashMap<>();

    public ActiveService() {
        this.activeSince = Instant.now().toEpochMilli();
    }


    public String getWebProtocol() {
        return webProtocol;
    }

    public void setWebProtocol(String webProtocol) {
        this.webProtocol = webProtocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getActiveSince() {
        return activeSince;
    }

    public AwakeningType getAwakening() {
        return awakening;
    }

    public void setAwakening(AwakeningType awakening) {
        this.awakening = awakening;
    }

    public Set<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Set<String> endpoints) {
        this.endpoints = endpoints;
    }

    public Set<String> getSockets() {
        return sockets;
    }

    public void setSockets(Set<String> sockets) {
        this.sockets = sockets;
    }

    public Set<String> getStaticResources() {
        return staticResources;
    }

    public void setStaticResources(Set<String> staticResources) {
        this.staticResources = staticResources;
    }

    public Map<String, Set<MenuItem>> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Map<String, Set<MenuItem>> menuItems) {
        this.menuItems = menuItems;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }



    @Override
    public String toString() {
        return "ActiveService{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", activeSince=" + activeSince +
                ", awakening=" + awakening +
                ", endpoints=" + endpoints +
                ", sockets=" + sockets +
                ", staticResources=" + staticResources +
                ", menuItems=" + menuItems +
                '}';
    }
}
