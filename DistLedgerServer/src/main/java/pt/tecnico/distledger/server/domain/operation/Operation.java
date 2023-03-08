package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class Operation {
    private String account;

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

}
