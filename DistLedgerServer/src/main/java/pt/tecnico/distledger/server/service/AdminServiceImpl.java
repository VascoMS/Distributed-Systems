package pt.tecnico.distledger.server.service;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.VectorClock;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.admin.*;

import java.util.stream.Collectors;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.UNAVAILABLE;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

    private final ServerState server;

    public AdminServiceImpl(ServerState server){
        this.server = server;
    }

    @Override
    public synchronized void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
        ServerState.AdminOperationResult result = server.activateServer();
        if(result == ServerState.AdminOperationResult.SERVER_ALREADY_ACTIVE){
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
    public synchronized void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
        ServerState.AdminOperationResult result = server.deactivateServer();
        if(result == ServerState.AdminOperationResult.SERVER_ALREADY_INACTIVE){
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
    public synchronized void getLedgerState(getLedgerStateRequest request, StreamObserver<getLedgerStateResponse> responseObserver) {
        ServerState.AdminOperationResult result = server.ledgerStateVerification(request.getPrev().getTimestampList());
        if(result == ServerState.AdminOperationResult.OUT_OF_DATE){
            responseObserver.onError(UNAVAILABLE.withDescription("Awaiting synchronization").asRuntimeException());
        }
        else {
            DistLedgerCommonDefinitions.LedgerState ledgerState = DistLedgerCommonDefinitions.LedgerState.newBuilder()
                    .addAllLedger(server.getLedger().stream().map(Operation::getOperationMessageFormat)
                            .collect(Collectors.toList())).build();
            VectorClock valueTS = server.getValueTs();
            getLedgerStateResponse response = getLedgerStateResponse.newBuilder().setLedgerState(ledgerState).setNew(DistLedgerCommonDefinitions.Timestamp.newBuilder().addAllTimestamp(valueTS.getTimestamps()).build()).build();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }
}

