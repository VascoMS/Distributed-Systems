package pt.tecnico.distledger.namingserver.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NamingServerState {

    private final Map<String, ServiceEntry> services;

    public enum NamingServerResult {
        OK,
        SERVICE_NOT_FOUND,
        SERVER_NOT_REGISTERED,
    }

    public NamingServerState() {
        this.services = new HashMap<>();
    }

    public Map<String, ServiceEntry> getServices() {
        return services;
    }

    public void addService(String serviceName, ServiceEntry serviceEntry) {
        this.services.put(serviceName, serviceEntry);
    }

    public List<ServerEntry> lookup(String service, String qualifier) {
        return services.get(service).getServerEntryList().stream()
                .filter(serverEntry -> serverEntry.getQualifier().equals(qualifier)).collect(Collectors.toList());

    }
    public List<ServerEntry> lookupAll(String service){
        return services.get(service).getServerEntryList();
    }

    public NamingServerResult register(String serviceName, String qualifier, String serverAddress) {
        ServiceEntry serviceEntry;
        if (services.containsKey(serviceName)) {
            serviceEntry = services.get(serviceName);

        } else {
            serviceEntry = new ServiceEntry(serviceName);
            addService(serviceName, serviceEntry);
        }
        if (serviceEntry.checkServerEntryExists(serverAddress, qualifier))
            return NamingServerResult.SERVER_NOT_REGISTERED;
        else {
            serviceEntry.addServerEntry(new ServerEntry(serverAddress, qualifier));
            return NamingServerResult.OK;
        }

    }

    public NamingServerResult delete(String serviceName, String target) {
        if (!getServices().containsKey(serviceName)) {
            return NamingServerResult.SERVICE_NOT_FOUND;
        }
        getServices().remove(serviceName);
        return NamingServerResult.OK;
    }
}