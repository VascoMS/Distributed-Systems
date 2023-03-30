package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.ArrayList;
import java.util.List;

public class Operation {
    private String account;
    private List<Integer> valueTS = new ArrayList<>();
    private List<Integer> replicaTS = new ArrayList<>();
    private boolean stable = false;

    public Operation(String fromAccount) {
        this.account = fromAccount;
    }

    public String getAccount() {
        return account;
    }

    public DistLedgerCommonDefinitions.Operation getOperationMessageFormat(){
        return DistLedgerCommonDefinitions.Operation.newBuilder()
                .setType(DistLedgerCommonDefinitions.OperationType.OP_UNSPECIFIED).setUserId(account).build();
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<Integer> getValueTS() {
        return this.valueTS;
    }
    
    public void setValueTS(List<Integer> valueTS) {
        this.valueTS = valueTS;
    }

    public List<Integer> getReplicaTS() {
        return this.replicaTS;
    }

    public void setReplicaTS(List<Integer> replicaTS) {
        this.replicaTS = replicaTS;
    }
    
    public boolean getStable() {
        return this.stable;
    }
    
    public void setStableTrue() {
        this.stable = true;
    }
}
