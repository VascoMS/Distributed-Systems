package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.List;

public class DeleteOp extends Operation {

    public DeleteOp(String account) {
        super(account);
    }

    @Override
    public DistLedgerCommonDefinitions.Operation getOperationMessageFormat(List<Integer> prevTS, List<Integer> TS){
        return DistLedgerCommonDefinitions.Operation.newBuilder()
                .setType(DistLedgerCommonDefinitions.OperationType.OP_DELETE_ACCOUNT)
                .setUserId(getAccount())
                .setPrevTS(DistLedgerCommonDefinitions.Timestamp.newBuilder().addAllTimestamp(prevTS).build())
                .setTS(DistLedgerCommonDefinitions.Timestamp.newBuilder().addAllTimestamp(TS).build())
                .build();
    }

    @Override
    public String toString() {
        return "  ledger {\n    type: OP_DELETE_ACCOUNT\n    userId: \"" + getAccount() + "\"\n  }";      
    }

}
