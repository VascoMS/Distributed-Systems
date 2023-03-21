package pt.tecnico.distledger.server.service;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.*;

public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {
    
    @Override
    public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver){
        
    }
}
