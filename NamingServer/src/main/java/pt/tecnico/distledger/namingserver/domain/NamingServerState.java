package pt.tecnico.distledger.namingserver.domain;

import java.util.*;
import java.util.stream.Collectors;

public class NamingServerState {

    private final Map<String, ServiceEntry> services;

    public enum NamingServerResult {
        OK,
        SERVICE_NOT_FOUND,
        SERVER_ALREADY_REGISTERED,
    }

    public NamingServerState() {
        this.services = new HashMap<>();
    }

    public Map<String, ServiceEntry> getServices() {
        return Collections.unmodifiableMap(this.services);
    }

    public void addService(String serviceName, ServiceEntry serviceEntry) {
        this.services.put(serviceName, serviceEntry);
    }

    public List<ServerEntry> lookup(String service, String qualifier) {
        if(services.get(service) == null)
            addService(service, new ServiceEntry(service));
        if(qualifier.isEmpty())
            return services.get(service).getServerEntryList();
        return services.get(service).getServerEntryList().stream()
                .filter(serverEntry -> serverEntry.getQualifier().equals(qualifier)).collect(Collectors.toList());

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
            return NamingServerResult.SERVER_ALREADY_REGISTERED;
        else {
            serviceEntry.addServerEntry(new ServerEntry(serverAddress, qualifier));
            return NamingServerResult.OK;
        }

    }

    public NamingServerResult delete(String serviceName, String target) {
        if (!getServices().containsKey(serviceName)) {
            return NamingServerResult.SERVICE_NOT_FOUND;
        }
        List<ServerEntry> svList = this.services.get(serviceName).getServerEntryList();
        svList.removeIf(svEntry -> svEntry.getTarget().equals(target));
        return NamingServerResult.OK;
    }
}