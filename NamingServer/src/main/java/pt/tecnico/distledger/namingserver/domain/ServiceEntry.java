package pt.tecnico.distledger.namingserver.domain;

import pt.tecnico.distledger.namingserver.domain.ServerEntry;

import java.util.ArrayList;
import java.util.List;

public class ServiceEntry {

    private List<ServerEntry> serverEntryList;
    private String serviceName;

    public ServiceEntry(String serviceName) {
        this.serviceName = serviceName;
        this.serverEntryList = new ArrayList<>();
    }

    public List<ServerEntry> getServerEntryList() {
        return this.serverEntryList;
    }

    public boolean checkServerEntryExists(String target, String qualifier){
        for(ServerEntry entry: serverEntryList){
            if(entry.getTarget().compareTo(target)==0 && entry.getQualifier().compareTo(qualifier)==0)
                return true;
        }
        return false;
    }

    public void setServerEntryList(List<ServerEntry> serverEntryList) {
        this.serverEntryList = serverEntryList;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void addServerEntry(ServerEntry serverEntry) {
        serverEntryList.add(serverEntry);
    }

    public void removeServerEntry(ServerEntry serverEntry) {
        serverEntryList.remove(serverEntry);
    }

}
