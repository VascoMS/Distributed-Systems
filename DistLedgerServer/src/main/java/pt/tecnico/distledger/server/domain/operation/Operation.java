package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.VectorClock;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

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

    public DistLedgerCommonDefinitions.Operation getOperationMessageFormat(List<Integer> prevTS, List<Integer> TS){
        return DistLedgerCommonDefinitions.Operation.newBuilder()
                .setType(DistLedgerCommonDefinitions.OperationType.OP_UNSPECIFIED)
                .setUserId(account)
                .setPrevTS(DistLedgerCommonDefinitions.Timestamp.newBuilder().addAllTimestamp(prevTS).build())
                .setTS(DistLedgerCommonDefinitions.Timestamp.newBuilder().addAllTimestamp(TS).build())
                .build();
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public VectorClock getTS() {
        return this.TS;
    }
    
    public void setTS(VectorClock valueTS) {
        this.TS = new VectorClock(valueTS.getTimestamps());
    }

    public VectorClock getPrev() {
        return this.prev;
    }

    public void setPrev(VectorClock replicaTS) {
        this.prev = new VectorClock(replicaTS.getTimestamps());
    }
    
    public boolean getStable() {
        return this.stable;
    }
    
    public void setStableTrue() {
        this.stable = true;
    }
}
