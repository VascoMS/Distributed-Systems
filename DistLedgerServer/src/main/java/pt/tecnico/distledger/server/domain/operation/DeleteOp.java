package pt.tecnico.distledger.server.domain.operation;

public class DeleteOp extends Operation {

    public DeleteOp(String account) {
        super(account);
    }

    @Override
    public String toString() {
        return "  ledger {\n    type: OP_DELETE_ACCOUNT\n    userId: \"" + getAccount() + "\"\n  }";      
    }

}
