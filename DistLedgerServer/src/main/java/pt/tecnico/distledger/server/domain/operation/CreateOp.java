package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.List;

public class CreateOp extends Operation {

    public CreateOp(String account) {
        super(account);
    }

    @Override
    public DistLedgerCommonDefinitions.Operation getOperationMessageFormat(List<Integer> prevTS, List<Integer> TS){
        return DistLedgerCommonDefinitions.Operation.newBuilder()
                .setType(DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT)
                .setUserId(getAccount())
                .setPrevTS(DistLedgerCommonDefinitions.Timestamp.newBuilder().addAllTimestamp(prevTS).build())
                .setTS(DistLedgerCommonDefinitions.Timestamp.newBuilder().addAllTimestamp(TS).build())
                .build();
    }

    @Override
    public String toString() {
        return "  ledger {\n    type: OP_CREATE_ACCOUNT\n    userId: \"" + getAccount() + "\"\n  }";      
    }
}
