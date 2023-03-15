package pt.tecnico.distledger.namingserver.services;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.namingserver.domain.NamingServer;
import pt.tecnico.distledger.namingserver.domain.ServerEntry;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;

import java.util.List;

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

    }
}
