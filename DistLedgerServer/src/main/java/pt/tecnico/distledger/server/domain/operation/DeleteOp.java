package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class DeleteOp extends Operation {

    public DeleteOp(String account) {
        super(account);
    }

    @Override
    public DistLedgerCommonDefinitions.Operation getOperationMessageFormat(){
        return DistLedgerCommonDefinitions.Operation.newBuilder()
                .setType(DistLedgerCommonDefinitions.OperationType.OP_DELETE_ACCOUNT).setUserId(getAccount()).build();
    }

    @Override
    public String toString() {
        return "  ledger {\n    type: OP_DELETE_ACCOUNT\n    userId: \"" + getAccount() + "\"\n  }";      
    }

}
