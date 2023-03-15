package pt.tecnico.distledger.namingserver.services;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.namingserver.domain.NamingServer;
import pt.tecnico.distledger.namingserver.domain.ServerEntry;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.user.TransferToResponse;

import java.util.List;

import static io.grpc.Status.INVALID_ARGUMENT;

public class NamingServerServiceImpl extends NamingServerServiceGrpc.NamingServerServiceImplBase{

    private final NamingServer server;

    public NamingServerServiceImpl(NamingServer server){
        this.server=server;
    }

    public NamingServer getServer() {
        return server;
    }

    @Override
    public void lookup(LookupRequest request, StreamObserver<LookupResponse> responseObserver){

    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver){

    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver){
        NamingServer.NamingServerResult result = server.delete(request.getServiceName(), request.getTarget());
        if(result == NamingServer.NamingServerResult.SERVICE_NOT_FOUND) {
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
