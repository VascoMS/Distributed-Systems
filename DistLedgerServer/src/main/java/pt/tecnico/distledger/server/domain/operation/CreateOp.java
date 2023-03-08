package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class CreateOp extends Operation {

    public CreateOp(String account) {
        super(account);
    }

    @Override
    public DistLedgerCommonDefinitions.Operation getOperationMessageFormat(){
        return DistLedgerCommonDefinitions.Operation.newBuilder()
                .setType(DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT).setUserId(getAccount()).build();
    }

    @Override
    public String toString() {
        return "  ledger {\n    type: OP_CREATE_ACCOUNT\n    userId: \"" + getAccount() + "\"\n  }";      
    }
}
