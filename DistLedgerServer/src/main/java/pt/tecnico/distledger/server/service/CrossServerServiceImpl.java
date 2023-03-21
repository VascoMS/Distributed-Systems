package pt.tecnico.distledger.server.service;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;


public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {
    
    @Override
    public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver){
        
    }
}
