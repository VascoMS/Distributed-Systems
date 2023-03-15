package pt.tecnico.distledger.namingserver.domain;

import java.util.HashMap;
import java.util.Map;

public class NamingServer {
    private Map<String, ServiceEntry> services;

    public NamingServer() {
        this.services = new HashMap<>();
    }

    public Map<String, ServiceEntry> getServices() {
        return services;
    }

    public void addServices(String serviceName ,ServiceEntry serviceEntry) {
        this.services.put(serviceName, serviceEntry);
    }


}