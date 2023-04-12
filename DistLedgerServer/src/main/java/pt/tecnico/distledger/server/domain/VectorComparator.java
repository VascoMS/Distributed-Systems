package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.Operation;

import java.util.Comparator;

public class VectorComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation op1, Operation op2) {
        for (int i = 0; i < 3; i++) {
            if (op1.getTS().getTimestamps().get(i) > op2.getTS().getTimestamps().get(i)) {
                return 1;
            } else if (op1.getTS().getTimestamps().get(i) < op2.getTS().getTimestamps().get(i)){
                return -1;
            }
        }
        return 0;
    }
}
