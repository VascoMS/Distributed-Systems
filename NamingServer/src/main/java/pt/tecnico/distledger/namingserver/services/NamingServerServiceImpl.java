package pt.tecnico.distledger.namingserver.services;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;

import java.util.stream.Collectors;

import static io.grpc.Status.INVALID_ARGUMENT;

public class NamingServerServiceImpl extends NamingServerServiceGrpc.NamingServerServiceImplBase{

    private final NamingServerState server;

    public NamingServerServiceImpl(NamingServerState server){
        this.server=server;
    }

    public NamingServerState getServer() {
        return server;
    }

    @Override
    public void lookup(LookupRequest request, StreamObserver<LookupResponse> responseObserver){
        LookupResponse response = LookupResponse.newBuilder().addAllServer(server.lookup(request.getServiceName(),
                request.getQualifier()).stream().map(serverEntry -> Server.newBuilder().setServerTarget(serverEntry.getTarget())
                        .setQualifier(serverEntry.getQualifier()).build()).collect(Collectors.toList())).build();
        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver){
        NamingServerState.NamingServerResult result = server.register(request.getServiceName(), request.getQualifier(), request.getServerAddress());
        if(result == NamingServerState.NamingServerResult.SERVER_ALREADY_REGISTERED){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Server already registered").asRuntimeException());
        }
        RegisterResponse response = RegisterResponse.getDefaultInstance();
        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();

    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver){
        NamingServerState.NamingServerResult result = server.delete(request.getServiceName(), request.getTarget());
        if(result == NamingServerState.NamingServerResult.SERVICE_NOT_FOUND) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Not possible to remove the server").asRuntimeException());
        }
        else {
            DeleteResponse response = DeleteResponse.getDefaultInstance();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }
}
