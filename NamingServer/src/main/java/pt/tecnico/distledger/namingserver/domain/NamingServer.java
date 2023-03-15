package pt.tecnico.distledger.namingserver.domain;

import java.util.HashMap;
import java.util.Map;

public class NamingServer {

    public enum NamingServerResult {
        OK,
        SERVICE_NOT_FOUND,
        SERVER_NOT_REGISTERED,
    }
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

    public NamingServerResult delete(String serviceName, String target) {
        if(!getServices().containsKey(serviceName)){
            return NamingServerResult.SERVICE_NOT_FOUND;
        }
        getServices().remove(serviceName);
        return NamingServerResult.OK;
    }
}