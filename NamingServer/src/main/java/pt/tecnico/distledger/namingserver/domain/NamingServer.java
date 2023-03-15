package pt.tecnico.distledger.namingserver.domain;

import java.util.HashMap;
import java.util.Map;

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

    public void addService(String serviceName ,ServiceEntry serviceEntry) {
        this.services.put(serviceName, serviceEntry);
    }

    public NamingServerResult register(String serviceName, String qualifier, String serverAddress){
        if(services.containsKey(serviceName)){
            ServiceEntry serviceEntry = services.get(serviceName);
          
        }
        else{
            ServiceEntry serviceEntry = new ServiceEntry(serviceName);
            addServices(serviceName,serviceEntry);
        }
        if(serviceEntry.checkServerEntryExists(serverAddress,qualifier))
            return NamingServerResult.SERVER_NOT_REGISTERED;
        else{
        serviceEntry.addServerEntry(new ServerEntry(serverAddress,qualifier))
        return NamingServerResult.OK;
        }

    }


}