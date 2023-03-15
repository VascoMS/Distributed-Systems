package pt.tecnico.distledger.namingserver.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NamingServer {
    private Map<String, ServiceEntry> services;

    public enum NamingServerResult {
        OK,
        SERVICE_NOT_FOUND,
        SERVER_NOT_REGISTERED,
    }

    public NamingServer() {
        this.services = new HashMap<>();
    }

    public Map<String, ServiceEntry> getServices() {
        return services;
    }

    public void addServices(String serviceName ,ServiceEntry serviceEntry) {
        this.services.put(serviceName, serviceEntry);
    }

    public List<ServerEntry> lookup(String service, String qualifier){
        return services.get(service).getServerEntryList().stream()
                .filter(serverEntry -> serverEntry.getQualifier().equals(qualifier)).collect(Collectors.toList());

    }


}