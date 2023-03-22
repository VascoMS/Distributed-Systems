package pt.tecnico.distledger.namingserver.domain;

public class ServerEntry {

    private String target;
    private String qualifier;

    public ServerEntry(String target, String qualifier){
        this.target = target;
        this.qualifier = qualifier;
    }

    public String getTarget() { return this.target;}

    public void setTarget(String target) {
        this.target = target;
    }

    public String getQualifier(){
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }
}