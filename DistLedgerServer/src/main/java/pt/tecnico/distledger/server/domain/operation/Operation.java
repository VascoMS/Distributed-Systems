package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.VectorClock;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.ArrayList;
import java.util.List;

public class Operation {
    private String account;
    private VectorClock TS = new VectorClock();
    private VectorClock prev = new VectorClock();
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

    public VectorClock getTS() {
        return this.TS;
    }
    
    public void setTS(VectorClock valueTS) {
        this.TS = valueTS;
    }

    public VectorClock getPrev() {
        return this.prev;
    }

    public void setPrev(VectorClock replicaTS) {
        this.prev = replicaTS;
    }
    
    public boolean getStable() {
        return this.stable;
    }
    
    public void setStableTrue() {
        this.stable = true;
    }
}
