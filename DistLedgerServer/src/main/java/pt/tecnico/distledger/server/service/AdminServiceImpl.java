package pt.tecnico.distledger.server.service;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.admin.*;

import static io.grpc.Status.INVALID_ARGUMENT;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private ServerState server = new ServerState();

    @Override
    public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
        OperationResult result = server.activateServer();
        if(result == OperationResult.SERVER_ALREADY_ACTIVE){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Server already active").asRuntimeException());
        }
        else{
            ActivateResponse response = ActivateResponse.getDefaultInstance();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
        OperationResult result = server.deactivateServer();
        if(result == OperationResult.SERVER_ALREADY_INACTIVE){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Server already inactive").asRuntimeException());
        }
        else{
            DeactivateResponse response = DeactivateResponse.getDefaultInstance();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getLedgerState(getLedgerStateRequest request, StreamObserver<getLedgerStateResponse> responseObserver) {
        getLedgerStateResponse response = getLedgerStateResponse.newBuilder().setLedgerState(ledger).build();
        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }
}

