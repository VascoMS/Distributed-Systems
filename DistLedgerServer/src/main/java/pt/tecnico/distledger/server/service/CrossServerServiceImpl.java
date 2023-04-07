package pt.tecnico.distledger.server.service;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.PropagateStateRequest;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.PropagateStateResponse;

import static io.grpc.Status.UNAVAILABLE;

//import java.util.stream.Collectors;


public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

    private final ServerState server;

    public CrossServerServiceImpl(ServerState server){this.server = server;}

    @Override
    public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver){
        server.updateState(request.getState(), request.getReplicaTs());
        PropagateStateResponse response = PropagateStateResponse.getDefaultInstance();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
