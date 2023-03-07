package pt.tecnico.distledger.server.domain.operation;

public class CreateOp extends Operation {

    public CreateOp(String account) {
        super(account);
    }

    @Override
    public String toString() {
        return "  ledger {\n    type: OP_CREATE_ACCOUNT\n    userId: \"" + getAccount() + "\"\n  }";      
    }
}
