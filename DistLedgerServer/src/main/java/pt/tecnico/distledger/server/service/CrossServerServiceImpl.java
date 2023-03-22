package pt.tecnico.distledger.server.service;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.PropagateStateRequest;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.PropagateStateResponse;

import static io.grpc.Status.UNAVAILABLE;

import java.util.stream.Collectors;


public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

    private final ServerState server;

    public CrossServerServiceImpl(ServerState server){this.server = server;}
    
    @Override
    public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver){
        ServerState.OperationResult result = server.updateState(request.getState().getLedgerList().stream().map(
                operation -> {
                    if(operation.getType() == OperationType.OP_CREATE_ACCOUNT)
                        return new CreateOp(operation.getUserId());
                    else if(operation.getType() == OperationType.OP_DELETE_ACCOUNT)
                        return new DeleteOp(operation.getUserId());
                    else if(operation.getType() == OperationType.OP_TRANSFER_TO)
                        return new TransferOp(operation.getUserId(), operation.getDestUserId(), operation.getAmount());
                    else
                        return new Operation(operation.getUserId());
                }).collect(Collectors.toList()));
        if(result == ServerState.OperationResult.SERVER_OFF){
            responseObserver.onError(UNAVAILABLE.withDescription("Secondary server is unavailable").asRuntimeException());
        }
        responseObserver.onNext(PropagateStateResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
